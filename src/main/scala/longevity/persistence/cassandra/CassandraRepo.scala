package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Session
import com.typesafe.config.Config
import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import emblem.exceptions.CouldNotTraverseException
import emblem.jsonUtil.dateTimeFormatter
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import java.util.UUID
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.realized.BasicPropComponent
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

// TODO please split this up a bit

/** a Cassandra repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param subdomain the subdomain containing the persistent that this repo persists
 * @param session the connection to the cassandra database
 */
private[longevity] class CassandraRepo[P <: Persistent] private (
  pType: PType[P],
  subdomain: Subdomain,
  protected val session: Session)
extends BaseRepo[P](pType, subdomain)
with CassandraSchema[P]
with CassandraCreate[P]
with CassandraRetrieveKeyVal[P]
with CassandraQuery[P]
with CassandraUpdate[P]
with CassandraDelete[P] {

  protected[cassandra] val tableName = typeKeyToTableName(pTypeKey)

  protected def typeKeyToTableName(key: TypeKey[_]) = camelToUnderscore(typeName(key.tpe))

  protected[cassandra] def actualizedComponents: List[BasicPropComponent[_ >: P <: Persistent, _, _]] = {
    val keyComponents = realizedPType.keySet.flatMap {
      _.realizedProp.basicPropComponents: Seq[BasicPropComponent[_ >: P <: Persistent, _, _]]
    }

    val indexComponents: Set[BasicPropComponent[_ >: P <: Persistent, _, _]] = {
      val props = pType.indexSet.flatMap(_.props)
      val realizedProps = props.map(realizedPType.realizedProps(_))
      realizedProps.map(_.basicPropComponents).flatten
    }

    (keyComponents ++ indexComponents).toList
  }

  protected val emblematicToJsonTranslator = new EmblematicToJsonTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  protected val jsonToEmblematicTranslator = new JsonToEmblematicTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  protected def columnName(prop: BasicPropComponent[_, _, _]) = "prop_" + scoredPath(prop)

  protected def scoredPath(prop: BasicPropComponent[_, _, _]) = prop.outerPropPath.inlinedPath.replace('.', '_')

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
    if (includeId)
      "id" +: "p" +: realizedPropColumnNames
    else
      "p" +: realizedPropColumnNames
  }

  protected def updateColumnValues(uuid: UUID, p: P, includeId: Boolean = true): Seq[AnyRef] = {
    val actualizedComponentValues = actualizedComponents.toSeq.sortBy(columnName).map { component =>
      def bind[PP >: P <: Persistent](componeent: BasicPropComponent[PP, _, _]) = propValBinding(component, p)
      bind(component)
    }
    if (includeId)
      uuid +: jsonStringForP(p) +: actualizedComponentValues
    else
      jsonStringForP(p) +: actualizedComponentValues
  }

  protected def propValBinding[PP >: P <: Persistent, A](
    component: BasicPropComponent[PP, _, A],
    p: P)
  : AnyRef = {
    def bind[B : TypeKey](component: BasicPropComponent[PP, _, B]) =
      cassandraValue(component.outerPropPath.get(p), component)
    bind(component)(component.outerPropPath.typeKey)
  }

  protected def cassandraValue[A : TypeKey](
    value: A,
    component: BasicPropComponent[_ >: P <: Persistent, _, A])
  : AnyRef = {
    value match {
      case id: CassandraId[_] => id.uuid
      case char: Char => char.toString
      case d: DateTime => dateTimeFormatter.print(d)
      case _ => value.asInstanceOf[AnyRef]
    }
  }

  protected def retrieveFromBoundStatement(
    statement: BoundStatement)(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] =
    Future {
      val resultSet = blocking {
        session.execute(statement)
      }
      val rowOption = Option(resultSet.one)
      rowOption.map(retrieveFromRow)
    }

  protected def retrieveFromRow(row: Row): PState[P] = {
    val id = CassandraId[P](row.getUUID("id"))
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    new PState[P](id, p)
  }

  override def toString = s"CassandraRepo[${pTypeKey.name}]"

}

private[persistence] object CassandraRepo {

  def sessionFromConfig(config: Config): Session = {
    val builder = Cluster.builder.addContactPoint(config.getString("cassandra.address"))
    if (config.getBoolean("cassandra.useCredentials")) {
      builder.withCredentials(
        config.getString("cassandra.username"),
        config.getString("cassandra.password"))
    }
    val cluster = builder.build
    val session = cluster.connect()
    val keyspace = config.getString("cassandra.keyspace")
    val replicationFactor = config.getInt("cassandra.replicationFactor")
    session.execute(
      s"""|
      |CREATE KEYSPACE IF NOT EXISTS $keyspace
      |WITH replication = {
      |  'class': 'SimpleStrategy',
      |  'replication_factor': $replicationFactor
      |};
      |""".stripMargin)
    session.execute(s"use $keyspace")
    session
  }

  def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    session: Session,
    polyRepoOpt: Option[CassandraRepo[_ >: P <: Persistent]])
  : CassandraRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new CassandraRepo(pType, subdomain, session) with PolyCassandraRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: CassandraRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: CassandraRepo[Poly] = poly
          }
          with CassandraRepo(pType, subdomain, session) with DerivedCassandraRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new CassandraRepo(pType, subdomain, session)
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
