package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.persistence._
import longevity.subdomain._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.retrievePersistedAssoc */
private[cassandra] trait CassandraRetrieveAssoc[R <: Root] {
  repo: CassandraRepo[R] =>

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[R])(
    implicit context: ExecutionContext)
  : Future[Option[PState[R]]] = {
    val id = assoc.asInstanceOf[CassandraId[R]]
    retrieveFromBoundStatement(bindIdSelectStatement(id))
  }

  private lazy val idSelectStatement = {
    val cql = s"SELECT * FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindIdSelectStatement(id: CassandraId[R]): BoundStatement = {
    idSelectStatement.bind(id.uuid)
  }

}
