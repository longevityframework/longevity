package longevity.persistence.jdbc

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of JdbcRepo.delete */
private[jdbc] trait JdbcDelete[P] {
  repo: JdbcRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] = Future {
    blocking {
      logger.debug(s"calling JdbcRepo.delete: $state")
      validateStablePrimaryKey(state)
      val rowCount = bindDeleteStatement(state).executeUpdate()
      if (persistenceConfig.optimisticLocking && rowCount != 1) {
        throw new WriteConflictException(state)
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling JdbcRepo.delete: $deleted")
      deleted
    }
  }

  private def bindDeleteStatement(state: PState[P]) = {
    val preparedStatement = connection.prepareStatement(deleteStatementSql)
    val bindings = if (persistenceConfig.optimisticLocking) {
      whereBindings(state) :+ state.rowVersionOrNull
    } else {
      whereBindings(state)
    }
    logger.debug(s"invoking SQL: $deleteStatementSql with bindings $bindings")
    bindings.zipWithIndex.foreach { case (binding, index) =>
      preparedStatement.setObject(index + 1, binding)
    }
    preparedStatement
  }

  private def deleteStatementSql: String = if (persistenceConfig.optimisticLocking) {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |AND
    |  row_version = :row_version
    |""".stripMargin
  } else {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |""".stripMargin
  }

}
