package longevity.persistence.jdbc

import java.util.UUID
import longevity.persistence.PState
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of JdbcRepo.create */
private[jdbc] trait JdbcCreate[M, P] {
  repo: JdbcRepo[M, P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    logger.debug(s"calling JdbcRepo.create: $p")
    val id = if (hasPrimaryKey) None else Some(JdbcId[P](UUID.randomUUID))
    val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
    val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
    val state = PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
    blocking {
      try {
        bindInsertStatement(state).executeUpdate()
      } catch convertDuplicateKeyException(state)
    }
    logger.debug(s"done calling JdbcRepo.create: $state")
    state
  }

  private def bindInsertStatement(state: PState[P]) = {
    val insertStatement = connection.prepareStatement(insertSql)
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
