package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.persistence._
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] =
    Future {
      session.execute(bindDeleteStatement(state))
      new Deleted(state.get, state.assoc)
    }

  private lazy val deleteStatement: PreparedStatement = {
    val cql = s"DELETE FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.assoc.asInstanceOf[CassandraId[P]].uuid
    boundStatement.bind(uuid)
  }

}
