package longevity.persistence.sqlite

import longevity.config.PersistenceConfig
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.PState
import longevity.persistence.jdbc.JdbcRepo
import org.sqlite.SQLiteException

/** a SQLite repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param modelType the domain model containing the persistent that this repo persists
 * @param session the connection to the sqlite database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class SQLiteRepo[P] private (
  pType: PType[P],
  modelType: ModelType,
  sessionInfo: JdbcRepo.JdbcSessionInfo,
  persistenceConfig: PersistenceConfig)
extends JdbcRepo[P](pType, modelType, sessionInfo, persistenceConfig) {

  override protected def addColumn(columnName: String, columnType: String): Unit = {
    val sql = s"ALTER TABLE $tableName ADD COLUMN $columnName $columnType"
    logger.debug(s"executing SQL: $sql")
    try {
      connection.prepareStatement(sql).execute()
    } catch {
      // ignoring this exception is best approximation of ALTER TABLE ADD IF NOT EXISTS
      case e: SQLiteException if e.getMessage.contains("duplicate column name: ") =>
    }
  }

  override protected def convertDuplicateKeyException(state: PState[P]): PartialFunction[Throwable, Unit] = {
    case e: SQLiteException if e.getMessage.contains("UNIQUE constraint failed") =>
      throwDuplicateKeyValException(state.get, e)
  }

  private def throwDuplicateKeyValException(p: P, cause: SQLiteException): Nothing = {
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

  override def toString = s"SQLiteRepo[${pTypeKey.name}]"

}

private[persistence] object SQLiteRepo {

  def apply[P](
    pType: PType[P],
    modelType: ModelType,
    session: JdbcRepo.JdbcSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[SQLiteRepo[_ >: P]])
  : SQLiteRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new SQLiteRepo(pType, modelType, session, config) with PolySQLiteRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: SQLiteRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: SQLiteRepo[Poly] = poly
          }
          with SQLiteRepo(pType, modelType, session, config) with DerivedSQLiteRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new SQLiteRepo(pType, modelType, session, config)
    }
    repo
  }

}
