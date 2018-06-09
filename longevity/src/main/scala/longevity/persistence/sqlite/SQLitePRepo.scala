package longevity.persistence.sqlite

import longevity.config.PersistenceConfig
import longevity.effect.Effect
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.PState
import longevity.persistence.jdbc.JdbcConnection
import longevity.persistence.jdbc.JdbcPRepo
import org.sqlite.SQLiteException

private[longevity] class SQLitePRepo[F[_], M, P] private (
  effect: Effect[F],
  modelType: ModelType[M],
  pType: PType[M, P],
  persistenceConfig: PersistenceConfig,
  connection: JdbcConnection)
extends JdbcPRepo[F, M, P](effect, modelType, pType, persistenceConfig, connection) {

  override protected def addColumn(columnName: String, columnType: String): Unit = {
    val sql = s"ALTER TABLE $tableName ADD COLUMN $columnName $columnType"
    logger.debug(s"executing SQL: $sql")
    try {
      connection.execute(connection.prepareStatement(sql))
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

  def apply[F[_], M, P](
    effect: Effect[F],
    modelType: ModelType[M],
    pType: PType[M, P],
    config: PersistenceConfig,
    polyRepoOpt: Option[SQLitePRepo[F, M, _ >: P]],
    connection: JdbcConnection)
  : SQLitePRepo[F, M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new SQLitePRepo(effect, modelType, pType, config, connection) with PolySQLitePRepo[F, M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: SQLitePRepo[F, M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: SQLitePRepo[F, M, Poly] = poly
          }
          with SQLitePRepo(effect, modelType, pType, config, connection) with DerivedSQLitePRepo[F, M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new SQLitePRepo(effect, modelType, pType, config, connection)
    }
    repo
  }

}
