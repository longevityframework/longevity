package longevity.persistence.cassandra

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.exceptions.InvalidQueryException
import com.typesafe.scalalogging.LazyLogging
import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import emblem.exceptions.CouldNotTraverseException
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import longevity.config.CassandraConfig
import longevity.config.PersistenceConfig
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.exceptions.persistence.cassandra.KeyspaceDoesNotExistException
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.persistence.SchemaCreator
import longevity.model.DerivedPType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.DomainModel
import longevity.model.realized.RealizedPartitionKey
import longevity.model.realized.RealizedPropComponent
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a Cassandra repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param domainModel the domain model containing the persistent that this repo persists
 * @param sessionInfo the connection to the cassandra database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class CassandraRepo[P] private (
  pType: PType[P],
  domainModel: DomainModel,
  private val sessionInfo: CassandraRepo.CassandraSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, domainModel)
with CassandraSchema[P]
with CassandraCreate[P]
with CassandraRetrieve[P]
with CassandraQuery[P]
with CassandraUpdate[P]
with CassandraDelete[P]
with LazyLogging {

  protected lazy val session = sessionInfo.session

  protected[cassandra] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

  protected val partitionComponents = realizedPType.partitionKey match {
    case Some(key) => key.partitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val postPartitionComponents = realizedPType.partitionKey match {
    case Some(key) => key.postPartitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val partitionKeyComponents = partitionComponents ++ postPartitionComponents

  protected val actualizedComponents =
    indexedComponents ++ (partitionKeyComponents: Seq[RealizedPropComponent[_ >: P, _, _]])

  protected[cassandra] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
    val keyComponents = realizedPType.keySet.filterNot(_.isInstanceOf[RealizedPartitionKey[_, _]]).flatMap {
      _.realizedProp.realizedPropComponents: Seq[RealizedPropComponent[_ >: P, _, _]]
    }

    val indexComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
      val props = pType.indexSet.flatMap(_.props)
      val realizedProps = props.map(realizedPType.realizedProps(_))
      realizedProps.map(_.realizedPropComponents).flatten
    }

    keyComponents ++ indexComponents
  }

  protected val emblematicToJsonTranslator = new EmblematicToJsonTranslator {
    override protected val emblematic = domainModel.emblematic
  }

  protected val jsonToEmblematicTranslator = new JsonToEmblematicTranslator {
    override protected val emblematic = domainModel.emblematic
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
        throw new NotInDomainModelTranslationException(e.typeKey.name, e)
    }
  }

  protected def updateColumnNames(isCreate: Boolean = true): Seq[String] = {
    def names(components: Set[RealizedPropComponent[_ >: P, _, _]]) =
      components.map(columnName).toSeq.sorted
    val componentColumnNames = if (isCreate) names(actualizedComponents) else names(indexedComponents)
    (isCreate && !hasPartitionKey, persistenceConfig.optimisticLocking) match {
      case (true,  true)  => "id" +: "row_version" +: "p" +: componentColumnNames
      case (true,  false) => "id" +:                  "p" +: componentColumnNames
      case (false, true)  =>         "row_version" +: "p" +: componentColumnNames
      case (false, false) =>                          "p" +: componentColumnNames
    }
  }

  protected def updateColumnValues(state: PState[P], isCreate: Boolean = true): Seq[AnyRef] = {
    def values(components: Set[RealizedPropComponent[_ >: P, _, _]]) =
      components.toSeq.sortBy(columnName).map { component => propValBinding(component, state.get) }
    val componentColumnValues = if (isCreate) values(actualizedComponents) else values(indexedComponents)
    def rv = state.rowVersionOrNull
    (isCreate && !hasPartitionKey, persistenceConfig.optimisticLocking) match {
      case (true,  true)  => uuid(state) +: rv +: jsonStringForP(state.get) +: componentColumnValues
      case (false, true)  =>                rv +: jsonStringForP(state.get) +: componentColumnValues
      case (true,  false) => uuid(state)       +: jsonStringForP(state.get) +: componentColumnValues
      case (false, false) =>                      jsonStringForP(state.get) +: componentColumnValues
    }
  }

  protected def uuid(state: PState[P]) = state.id.get.asInstanceOf[CassandraId[P]].uuid

  protected def whereAssignments = if (hasPartitionKey) {
    partitionKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  protected def whereBindings(state: PState[P]) = if (hasPartitionKey) {
    partitionKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[CassandraId[P]].uuid)
  }

  private def propValBinding[PP >: P, A](component: RealizedPropComponent[PP, _, A], p: P): AnyRef = {
    cassandraValue(component.outerPropPath.get(p))
  }

  protected def cassandraValue(value: Any): AnyRef = value match {
    case char: Char => char.toString
    case d: DateTime => cassandraDate(d)
    case _ => value.asInstanceOf[AnyRef]
  }

  protected def cassandraDate(d: DateTime) = new java.util.Date(d.getMillis)

  protected def retrieveFromRow(row: Row): PState[P] = {
    val id = if (!hasPartitionKey) {
      Some(CassandraId[P](row.getUUID("id")))
    } else {
      None
    }
    val rowVersion = if (persistenceConfig.optimisticLocking) {
      Option(row.getLong("row_version"))
    } else {
      None
    }
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    PState[P](id, rowVersion, p)
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
    blocking {
      session.close()
      session.getCluster.close()
      ()
    }
  }

  override def toString = s"CassandraRepo[${pTypeKey.name}]"

}

private[persistence] object CassandraRepo {

  case class CassandraSessionInfo(config: CassandraConfig) extends SchemaCreator {

    lazy val cluster = {
      val builder = Cluster.builder.addContactPoint(config.address)
      config.credentials.map { creds =>
        builder.withCredentials(creds.username, creds.password)
      }
      builder.build
    }

    lazy val session = {
      try {
        underlyingSession.execute(s"use ${config.keyspace}")
      } catch {
        case e: InvalidQueryException if
          e.getMessage.startsWith("Keyspace '") &&
          e.getMessage.endsWith("' does not exist") =>
          throw new KeyspaceDoesNotExistException(config, e)
      }
      underlyingSession
    }

    private lazy val underlyingSession = cluster.connect()

    def createSchema()(implicit context: ExecutionContext) = Future {
      blocking {
        underlyingSession.execute(
          s"""|
          |CREATE KEYSPACE IF NOT EXISTS ${config.keyspace}
          |WITH replication = {
          |  'class': 'SimpleStrategy',
          |  'replication_factor': ${config.replicationFactor}
          |};
          |""".stripMargin)
      }
    }

  }

  def apply[P](
    pType: PType[P],
    domainModel: DomainModel,
    session: CassandraSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[CassandraRepo[_ >: P]])
  : CassandraRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new CassandraRepo(pType, domainModel, session, config) with PolyCassandraRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: CassandraRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: CassandraRepo[Poly] = poly
          }
          with CassandraRepo(pType, domainModel, session, config) with DerivedCassandraRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new CassandraRepo(pType, domainModel, session, config)
    }
    repo
  }

  private[cassandra] val basicToCassandraType = Map[TypeKey[_], String](
    typeKey[Boolean]  -> "boolean",
    typeKey[Char]     -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double]   -> "double",
    typeKey[Float]    -> "float",
    typeKey[Int]      -> "int",
    typeKey[Long]     -> "bigint",
    typeKey[String]   -> "text")

}
