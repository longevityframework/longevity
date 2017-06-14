package longevity.persistence.sqlite

import java.sql.Connection
import longevity.config.PersistenceConfig
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.PState
import longevity.persistence.jdbc.JdbcPRepo
import org.sqlite.SQLiteException

private[longevity] class SQLitePRepo[M, P] private (
  pType: PType[M, P],
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  connection: () => Connection)
extends JdbcPRepo[M, P](pType, modelType, persistenceConfig, connection) {

  override protected def addColumn(columnName: String, columnType: String): Unit = {
    val sql = s"ALTER TABLE $tableName ADD COLUMN $columnName $columnType"
    logger.debug(s"executing SQL: $sql")
    try {
      connection().prepareStatement(sql).execute()
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

  override def toString = s"SQLitePRepo[${pTypeKey.name}]"

}

private[persistence] object SQLitePRepo {

  def apply[M, P](
    pType: PType[M, P],
    modelType: ModelType[M],
    config: PersistenceConfig,
    polyRepoOpt: Option[SQLitePRepo[M, _ >: P]],
    connection: () => Connection)
  : SQLitePRepo[M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new SQLitePRepo(pType, modelType, config, connection) with PolySQLitePRepo[M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: SQLitePRepo[M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: SQLitePRepo[M, Poly] = poly
          }
          with SQLitePRepo(pType, modelType, config, connection) with DerivedSQLitePRepo[M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new SQLitePRepo(pType, modelType, config, connection)
    }
    repo
  }

}
