package longevity.persistence.cassandra

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.typesafe.scalalogging.LazyLogging
import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import emblem.exceptions.CouldNotTraverseException
import emblem.jsonUtil.dateTimeFormatter
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import java.util.UUID
import longevity.context.CassandraConfig
import longevity.context.PersistenceConfig
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.realized.RealizedPropComponent
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a Cassandra repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param subdomain the subdomain containing the persistent that this repo persists
 * @param session the connection to the cassandra database
 * @param persistenceConfig persistence configuration that is persistence strategy agnostic
 */
private[longevity] class CassandraRepo[P <: Persistent] private (
  pType: PType[P],
  subdomain: Subdomain,
  private val sessionInfo: CassandraRepo.CassandraSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, subdomain)
with CassandraSchema[P]
with CassandraCreate[P]
with CassandraRetrieve[P]
with CassandraQuery[P]
with CassandraUpdate[P]
with CassandraDelete[P]
with LazyLogging {

  protected lazy val session = sessionInfo.session

  protected[cassandra] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

  protected[cassandra] def actualizedComponents: List[RealizedPropComponent[_ >: P <: Persistent, _, _]] = {
    val keyComponents = realizedPType.keySet.flatMap {
      _.realizedProp.realizedPropComponents: Seq[RealizedPropComponent[_ >: P <: Persistent, _, _]]
    }

    val indexComponents: Set[RealizedPropComponent[_ >: P <: Persistent, _, _]] = {
      val props = pType.indexSet.flatMap(_.props)
      val realizedProps = props.map(realizedPType.realizedProps(_))
      realizedProps.map(_.realizedPropComponents).flatten
    }

    (keyComponents ++ indexComponents).toList
  }

  protected val emblematicToJsonTranslator = new EmblematicToJsonTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  protected val jsonToEmblematicTranslator = new JsonToEmblematicTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  protected def columnName(prop: RealizedPropComponent[_, _, _]) = "prop_" + scoredPath(prop)

  protected def scoredPath(prop: RealizedPropComponent[_, _, _]) =
    prop.outerPropPath.inlinedPath.replace('.', '_')

  protected def jsonStringForP(p: P): String = {
    try {
      import org.json4s.native.JsonMethods._
      compact(render(emblematicToJsonTranslator.translate(p)(pTypeKey)))
    } catch {
      case e: CouldNotTraverseException =>
        throw new NotInSubdomainTranslationException(e.typeKey.name, e)
    }
  }

  protected def updateColumnNames(includeId: Boolean = true): Seq[String] = {
    val realizedPropColumnNames = actualizedComponents.map(columnName).toSeq.sorted
    (includeId, persistenceConfig.optimisticLocking) match {
      case (true,  true)  => "id" +: "modified_date" +: "p" +: realizedPropColumnNames
      case (false, true)  => "modified_date" +: "p" +: realizedPropColumnNames
      case (true,  false) => "id" +: "p" +: realizedPropColumnNames
      case (false, false) => "p" +: realizedPropColumnNames
    }
  }

  protected def updateColumnValues(uuid: UUID, modifiedDate: Option[DateTime], p: P, includeId: Boolean = true)
  : Seq[AnyRef] = {
    val actualizedComponentValues = actualizedComponents.toSeq.sortBy(columnName).map { component =>
      propValBinding(component, p)
    }
    def modDate = cassandraDate(modifiedDate.get)
    (includeId, persistenceConfig.optimisticLocking) match {
      case (true,  true)  => uuid +: modDate +: jsonStringForP(p) +: actualizedComponentValues
      case (false, true)  => modDate +: jsonStringForP(p) +: actualizedComponentValues
      case (true,  false) => uuid +: jsonStringForP(p) +: actualizedComponentValues
      case (false, false) => jsonStringForP(p) +: actualizedComponentValues
    }
  }

  private def propValBinding[PP >: P <: Persistent, A](
    component: RealizedPropComponent[PP, _, A],
    p: P)
  : AnyRef = {
    cassandraValue(component.outerPropPath.get(p))
  }

  protected def cassandraValue(value: Any): AnyRef = value match {
    case char: Char => char.toString
    case d: DateTime => cassandraDate(d)
    case _ => value.asInstanceOf[AnyRef]
  }

  protected def cassandraDate(d: DateTime): String = dateTimeFormatter.print(d)

  protected def retrieveFromRow(row: Row): PState[P] = {
    val id = CassandraId[P](row.getUUID("id"))
    val modifiedDate = if (persistenceConfig.optimisticLocking) {
      Option(row.getString("modified_date")).map(dateTimeFormatter.parseDateTime)
    } else {
      None
    }
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    PState[P](id, modifiedDate, p)
  }

  private var preparedStatements = Map[String, PreparedStatement]()

  protected def preparedStatement(cql: String): PreparedStatement = {
    synchronized {
      if (!preparedStatements.contains(cql)) {
        preparedStatements += cql -> session.prepare(cql)
      }
    }
    preparedStatements(cql)
  }

  override protected[persistence] def close()(implicit executionContext: ExecutionContext) = Future {
    session.close()
    session.getCluster.close()
    ()
  }

  override def toString = s"CassandraRepo[${pTypeKey.name}]"

}

private[persistence] object CassandraRepo {

  case class CassandraSessionInfo(config: CassandraConfig) {
    lazy val cluster = {
      val builder = Cluster.builder.addContactPoint(config.address)
      config.credentials.map { creds =>
        builder.withCredentials(creds.username, creds.password)
      }
      builder.build
    }
    lazy val session = {
      val session = cluster.connect()
      session.execute(
        s"""|
        |CREATE KEYSPACE IF NOT EXISTS ${config.keyspace}
        |WITH replication = {
        |  'class': 'SimpleStrategy',
        |  'replication_factor': ${config.replicationFactor}
        |};
        |""".stripMargin)
      session.execute(s"use ${config.keyspace}")
      session
    }
  }

  def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    session: CassandraSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[CassandraRepo[_ >: P <: Persistent]])
  : CassandraRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new CassandraRepo(pType, subdomain, session, config) with PolyCassandraRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: CassandraRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: CassandraRepo[Poly] = poly
          }
          with CassandraRepo(pType, subdomain, session, config) with DerivedCassandraRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new CassandraRepo(pType, subdomain, session, config)
    }
    repo.createSchema()
    repo
  }

  private[cassandra] val basicToCassandraType = Map[TypeKey[_], String](
    typeKey[Boolean] -> "boolean",
    typeKey[Char] -> "text",
    typeKey[DateTime] -> "text",
    typeKey[Double] -> "double",
    typeKey[Float] -> "float",
    typeKey[Int] -> "int",
    typeKey[Long] -> "bigint",
    typeKey[String] -> "text")

}
