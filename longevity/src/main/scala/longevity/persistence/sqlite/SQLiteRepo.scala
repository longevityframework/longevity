package longevity.persistence.sqlite

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
import longevity.config.PersistenceConfig
import longevity.config.SQLiteConfig
import longevity.exceptions.persistence.DuplicateKeyValException
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
import org.sqlite.SQLiteException
import scala.collection.mutable.WeakHashMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a SQLite repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param domainModel the domain model containing the persistent that this repo persists
 * @param session the connection to the sqlite database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class SQLiteRepo[P] private (
  pType: PType[P],
  domainModel: DomainModel,
  private val sessionInfo: SQLiteRepo.SQLiteSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, domainModel)
with SQLiteSchema[P]
with SQLiteCreate[P]
with SQLiteRetrieve[P]
with SQLiteQuery[P]
with SQLiteUpdate[P]
with SQLiteDelete[P]
with LazyLogging {

  protected lazy val connection = sessionInfo.connection

  protected[sqlite] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

  // TODO see if any of this is dead code

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

  protected[sqlite] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
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

  protected def uuid(state: PState[P]) = state.id.get.asInstanceOf[SQLiteId[P]].uuid

  protected def whereAssignments = if (hasPrimaryKey) {
    primaryKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  protected def whereBindings(state: PState[P]) = if (hasPrimaryKey) {
    primaryKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[SQLiteId[P]].uuid)
  }

  private def propValBinding[PP >: P, A](component: RealizedPropComponent[PP, _, A], p: P): AnyRef = {
    sqliteValue(component.outerPropPath.get(p))
  }

  protected def sqliteValue(value: Any): AnyRef = value match {
    case char: Char => char.toString
    case d: DateTime => sqliteDate(d)
    case _ => value.asInstanceOf[AnyRef]
  }

  protected def sqliteDate(d: DateTime) = new java.util.Date(d.getMillis)

  // totally assumes you already called resultSet.next() and it returned true
  protected def retrieveFromResultSet(resultSet: ResultSet): PState[P] = {
    val id = if (!hasPrimaryKey) {
      Some(SQLiteId[P](UUID.fromString(resultSet.getString("id"))))
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

  protected def throwDuplicateKeyValException(p: P, cause: SQLiteException): Nothing = {
    val columnRegex = """UNIQUE constraint failed: (?:\w+)\.(\w+)""".r.unanchored
    val name = cause.getMessage match {
      case columnRegex(name) => name
      case _ => throw cause
    }
    val realizedKey = realizedPType.keySet.find(key =>
      columnName(key.realizedProp.realizedPropComponents.head) == name
    ).getOrElse(throw cause)
    throw new DuplicateKeyValException(p, realizedKey.key, cause)
  }

  override protected[persistence] def close()(implicit executionContext: ExecutionContext) = Future {
    SQLiteRepo.releaseSharedConn(sessionInfo.config)
  }

  override def toString = s"SQLiteRepo[${pTypeKey.name}]"

}

private[persistence] object SQLiteRepo {

  // it's bad news to create multiple connections against a single SQLite database. this
  // is not a problem for typical programattic usage, where there is one LongevityContext,
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
  private val sharedConns = WeakHashMap[SQLiteConfig, SharedConn]()

  private def acquireSharedConn(config: SQLiteConfig): Connection = blocking {
    SQLiteRepo.synchronized {
      if (sharedConns.contains(config)) {
        val sc = sharedConns(config)
        sharedConns += config -> sc.copy(numHolders = sc.numHolders + 1)
        sc.conn
      } else {
        Class.forName(config.jdbcDriverClass)
        val conn = DriverManager.getConnection(config.jdbcUrl)
        sharedConns += config -> SharedConn(1, conn)
        conn
      }
    }
  }

  private def releaseSharedConn(config: SQLiteConfig): Unit = blocking {
    SQLiteRepo.synchronized {
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

  case class SQLiteSessionInfo(val config: SQLiteConfig) {
    lazy val connection = acquireSharedConn(config)
  }

  def apply[P](
    pType: PType[P],
    domainModel: DomainModel,
    session: SQLiteSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[SQLiteRepo[_ >: P]])
  : SQLiteRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new SQLiteRepo(pType, domainModel, session, config) with PolySQLiteRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: SQLiteRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: SQLiteRepo[Poly] = poly
          }
          with SQLiteRepo(pType, domainModel, session, config) with DerivedSQLiteRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new SQLiteRepo(pType, domainModel, session, config)
    }
    repo
  }

  private[sqlite] val basicToSQLiteType = Map[TypeKey[_], String](
    typeKey[Boolean]  -> "boolean",
    typeKey[Char]     -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double]   -> "double",
    typeKey[Float]    -> "float",
    typeKey[Int]      -> "int",
    typeKey[Long]     -> "bigint",
    typeKey[String]   -> "text")

}
