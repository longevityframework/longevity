package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] =
    Future {
      val resultSet = blocking {
        session.execute(bindDeleteStatement(state))
      }
      val deleteSuccess = resultSet.one.getBool(0)
      if (!deleteSuccess) {
        throw new WriteConflictException(state)
      }
      new Deleted(state.get)
    }

  private lazy val deleteStatement: PreparedStatement = preparedStatement(deleteStatementCql)

  protected def deleteStatementCql: String = if (persistenceConfig.optimisticLocking) {
    s"DELETE FROM $tableName WHERE id = :id IF modified_date = :modified_date"
  } else {
    s"DELETE FROM $tableName WHERE id = :id"
  }

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.id.asInstanceOf[CassandraId[P]].uuid
    if (persistenceConfig.optimisticLocking) {
      boundStatement.bind(uuid, state.modifiedDate.map(cassandraDate).orNull)
    } else {
      boundStatement.bind(uuid)
    }
  }

}
