package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraPRepo.delete */
private[cassandra] trait CassandraDelete[M, P] {
  repo: CassandraPRepo[M, P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] = Future {
    blocking {
      logger.debug(s"calling CassandraPRepo.delete: $state")
      validateStablePrimaryKey(state)
      val resultSet = session().execute(bindDeleteStatement(state))
      if (persistenceConfig.optimisticLocking) {
        val deleteSuccess = resultSet.one.getBool(0)
        if (!deleteSuccess) {
          throw new WriteConflictException(state)
        }
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling CassandraPRepo.delete: $deleted")
      deleted
    }
  }

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val bindings = if (persistenceConfig.optimisticLocking) {
      whereBindings(state) :+ state.rowVersionOrNull
    } else {
      whereBindings(state)
    }
    logger.debug(s"invoking CQL: ${deleteStatement.getQueryString} with bindings $bindings")
    boundStatement.bind(bindings: _*)
  }

  protected[cassandra] def deleteStatement = deleteStatementPreparedOnce

  private lazy val deleteStatementPreparedOnce = preparedStatement(deleteStatementCql)

  private def deleteStatementCql: String = if (persistenceConfig.optimisticLocking) {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |IF
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
