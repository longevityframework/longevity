package longevity.persistence.jdbc

import java.util.UUID
import longevity.effect.Effect.Syntax
import longevity.persistence.PState
import org.joda.time.DateTime

/** implementation of JdbcPRepo.create */
private[jdbc] trait JdbcCreate[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  override def create(p: P): F[PState[P]] = {
    val fp = effect.pure(p)
    val fs = effect.map(fp) { p =>
      logger.debug(s"executing JdbcPRepo.create: $p")
      val id = if (hasPrimaryKey) None else Some(JdbcId(UUID.randomUUID))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
      PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
    }
    val fs2 = effect.mapBlocking(fs)(createStateBlocking)
    effect.map(fs2) { s =>
      logger.debug(s"done executing JdbcPRepo.create: $s")
      s
    }
  }

  private[persistence] def createState(state: PState[P]): F[PState[P]] =
    effect.pure(state).mapBlocking(createStateBlocking)

  private def createStateBlocking(s: PState[P]): PState[P] = {
    try {
      bindInsertStatement(s).executeUpdate()
    } catch {
      convertDuplicateKeyException(s)
    }
    s
  }

  private def bindInsertStatement(state: PState[P]) = {
    val insertStatement = connection().prepareStatement(insertSql)
    val bindings = updateColumnValues(state, isCreate = true)
    logger.debug(s"invoking SQL: $insertStatement with bindings: $bindings")
    bindings.zipWithIndex.foreach { case (binding, index) =>
      insertStatement.setObject(index + 1, binding)
    }
    insertStatement
  }

  private lazy val insertSql = {
    val names = updateColumnNames(isCreate = true)
    val columns = names.mkString(",\n  ")
    val substitutionPatterns = names.map(c => s":$c").mkString(",\n  ")
    s"""|
    |INSERT INTO $tableName (
    |  $columns
    |) VALUES (
    |  $substitutionPatterns
    |)
    |""".stripMargin
  }

}
