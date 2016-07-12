package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.persistence._
import longevity.subdomain.persistent.Persistent
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] =
    Future {
      blocking {
        session.execute(bindDeleteStatement(state))
      }
      new Deleted(state.get)
    }

  private lazy val deleteStatement: PreparedStatement = session.prepare(deleteStatementCql)

  protected def deleteStatementCql: String = s"DELETE FROM $tableName WHERE id = :id"

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.id.asInstanceOf[CassandraId[P]].uuid
    boundStatement.bind(uuid)
  }

}
