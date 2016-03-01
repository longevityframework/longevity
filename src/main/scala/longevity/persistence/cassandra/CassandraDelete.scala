package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence._
import longevity.subdomain._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[R <: Root] {
  repo: CassandraRepo[R] =>

  override def delete(state: PState[R]): Future[Deleted[R]] = Future {
    session.execute(bindDeleteStatement(state))
    new Deleted(state.get, state.assoc)
  }

  private val deleteStatement: PreparedStatement = {
    val cql = s"DELETE FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindDeleteStatement(state: PState[R]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.assoc.asInstanceOf[CassandraId[R]].uuid
    boundStatement.bind(uuid)
  }

}
