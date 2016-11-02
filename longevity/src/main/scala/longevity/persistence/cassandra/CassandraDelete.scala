package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.Persistent
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] =
    Future {
      logger.debug(s"calling CassandraRepo.delete: $state")
      val resultSet = blocking {
        session.execute(bindDeleteStatement(state))
      }
      if (persistenceConfig.optimisticLocking) {
        val deleteSuccess = resultSet.one.getBool(0)
        if (!deleteSuccess) {
          throw new WriteConflictException(state)
        }
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling CassandraRepo.delete: $deleted")
      deleted
    }

  private lazy val deleteStatement: PreparedStatement = preparedStatement(deleteStatementCql)

  protected def deleteStatementCql: String = if (persistenceConfig.optimisticLocking) {
    s"DELETE FROM $tableName WHERE id = :id IF row_version = :row_version"
  } else {
    s"DELETE FROM $tableName WHERE id = :id"
  }

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.id.get.asInstanceOf[CassandraId[P]].uuid
    logger.debug(s"invoking CQL: ${deleteStatement.getQueryString} with uuid $uuid")
    if (persistenceConfig.optimisticLocking) {
      val version = if (state.rowVersion.isEmpty) null else state.rowVersion.get.asInstanceOf[AnyRef]
      boundStatement.bind(uuid, version)
    } else {
      boundStatement.bind(uuid)
    }
  }

}
