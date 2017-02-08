package longevity.persistence.jdbc

import com.typesafe.scalalogging.LazyLogging
import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import emblem.exceptions.CouldNotTraverseException
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.UUID
import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.DerivedPType
import longevity.model.DomainModel
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.realized.RealizedPrimaryKey
import longevity.model.realized.RealizedPropComponent
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import org.joda.time.DateTime
import scala.collection.mutable.WeakHashMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a Jdbc repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param domainModel the domain model containing the persistent that this repo persists
 * @param session the connection to the jdbc database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[persistence] class JdbcRepo[P] private[persistence] (
  pType: PType[P],
  domainModel: DomainModel,
  private val sessionInfo: JdbcRepo.JdbcSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, domainModel)
with JdbcSchema[P]
with JdbcCreate[P]
with JdbcRetrieve[P]
with JdbcQuery[P]
with JdbcUpdate[P]
with JdbcDelete[P]
with LazyLogging {

  protected lazy val connection = sessionInfo.connection

  protected[jdbc] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

  protected val partitionComponents = realizedPType.primaryKey match {
    case Some(key) => key.partitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val postPartitionComponents = realizedPType.primaryKey match {
    case Some(key) => key.postPartitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val primaryKeyComponents = partitionComponents ++ postPartitionComponents

  protected val actualizedComponents =
    indexedComponents ++ (primaryKeyComponents: Seq[RealizedPropComponent[_ >: P, _, _]])

  protected[jdbc] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
    val keyComponents = realizedPType.keySet.filterNot(_.isInstanceOf[RealizedPrimaryKey[_, _]]).flatMap {
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
    (isCreate && !hasPrimaryKey, persistenceConfig.optimisticLocking) match {
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
    (isCreate && !hasPrimaryKey, persistenceConfig.optimisticLocking) match {
      case (true,  true)  => uuid(state) +: rv +: jsonStringForP(state.get) +: componentColumnValues
      case (false, true)  =>                rv +: jsonStringForP(state.get) +: componentColumnValues
      case (true,  false) => uuid(state)       +: jsonStringForP(state.get) +: componentColumnValues
      case (false, false) =>                      jsonStringForP(state.get) +: componentColumnValues
    }
  }

  protected def uuid(state: PState[P]) = state.id.get.asInstanceOf[JdbcId[P]].uuid

  protected def whereAssignments = if (hasPrimaryKey) {
    primaryKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  protected def whereBindings(state: PState[P]) = if (hasPrimaryKey) {
    primaryKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[JdbcId[P]].uuid)
  }

  private def propValBinding[PP >: P, A](component: RealizedPropComponent[PP, _, A], p: P): AnyRef = {
    jdbcValue(component.outerPropPath.get(p))
  }

  protected def jdbcValue(value: Any): AnyRef = value match {
    case char: Char => char.toString
    case d: DateTime => jdbcDate(d)
    case _ => value.asInstanceOf[AnyRef]
  }

  protected def jdbcDate(d: DateTime) = new java.util.Date(d.getMillis)

  // totally assumes you already called resultSet.next() and it returned true
  protected def retrieveFromResultSet(resultSet: ResultSet): PState[P] = {
    val id = if (!hasPrimaryKey) {
      Some(JdbcId[P](UUID.fromString(resultSet.getString("id"))))
    } else {
      None
    }
    val rowVersion = if (persistenceConfig.optimisticLocking) {
      Option(resultSet.getLong("row_version"))
    } else {
      None
    }
    import org.json4s.native.JsonMethods._    
    val json = parse(resultSet.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    PState[P](id, rowVersion, p)
  }

  /** converts a duplicate key exception (ie unique constraint violation) from the underlying database
   * driver into a [[longevity.exceptions.persistence.DuplicateKeyValException]], and throws the new
   * exception
   */
  protected def convertDuplicateKeyException(state: PState[P]): PartialFunction[Throwable, Unit] =
    PartialFunction.empty

  override protected[persistence] def close()(implicit executionContext: ExecutionContext) = Future {
    JdbcRepo.releaseSharedConn(sessionInfo.config)
  }

  override def toString = s"JdbcRepo[${pTypeKey.name}]"

}

private[persistence] object JdbcRepo {

  // it's bad news to create multiple connections against a single Jdbc database. this
  // is not a problem for typical programatic usage, where there is one LongevityContext,
  // and thus one JDBC connection. but in my test suites it is a different story, as we
  // have several tests running in parallel, hitting the same test database.
  //
  // ideally, we would craft the test suite to share LongevityContexts when possible, but
  // even this would not be enough, because in the test suite, multiple contexts actually
  // target the same database (e.g., contexts with optimistic locking turned on and off).
  // so we would have to track the connections themselves, and figure out when to actually
  // close the connection. aside from being a real pain, this would make for some
  // convoluted tests. instead of doing this, we share the conns here. its a bit of an
  // overhead, but it might actually come in useful for some user somewhere. and we could
  // always hide it behind a configuration setting if any users complain about the
  // overhead.

  private case class SharedConn(numHolders: Int, conn: Connection)
  private val sharedConns = WeakHashMap[JdbcConfig, SharedConn]()

  private def acquireSharedConn(config: JdbcConfig): Connection = blocking {
    JdbcRepo.synchronized {
      if (sharedConns.contains(config)) {
        val sc = sharedConns(config)
        sharedConns += config -> sc.copy(numHolders = sc.numHolders + 1)
        sc.conn
      } else {
        Class.forName(config.driverClass)
        val conn = DriverManager.getConnection(config.url)
        sharedConns += config -> SharedConn(1, conn)
        conn
      }
    }
  }

  private def releaseSharedConn(config: JdbcConfig): Unit = blocking {
    JdbcRepo.synchronized {
      if (sharedConns.contains(config)) {
        val sc = sharedConns(config)
        if (sc.numHolders == 1) {
          sc.conn.close()
          sharedConns -= config
        } else {
          sharedConns += config -> sc.copy(numHolders = sc.numHolders - 1)
        }
      }
    }
  }

  case class JdbcSessionInfo(val config: JdbcConfig) {
    lazy val connection = acquireSharedConn(config)
  }

  def apply[P](
    pType: PType[P],
    domainModel: DomainModel,
    session: JdbcRepo.JdbcSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[JdbcRepo[_ >: P]])
  : JdbcRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new JdbcRepo(pType, domainModel, session, config) with PolyJdbcRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: JdbcRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: JdbcRepo[Poly] = poly
          }
          with JdbcRepo(pType, domainModel, session, config) with DerivedJdbcRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new JdbcRepo(pType, domainModel, session, config)
    }
    repo
  }

  private[jdbc] val basicToJdbcType = Map[TypeKey[_], String](
    typeKey[Boolean]  -> "boolean",
    typeKey[Char]     -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double]   -> "double",
    typeKey[Float]    -> "float",
    typeKey[Int]      -> "int",
    typeKey[Long]     -> "bigint",
    typeKey[String]   -> "text")

}
