package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.persistence._
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.retrievePersistedAssoc */
private[cassandra] trait CassandraRetrieveAssoc[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = {
    val id = assoc.asInstanceOf[CassandraId[P]]
    retrieveFromBoundStatement(bindIdSelectStatement(id))
  }

  private lazy val idSelectStatement = {
    val cql = s"SELECT * FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindIdSelectStatement(id: CassandraId[P]): BoundStatement = {
    idSelectStatement.bind(id.uuid)
  }

}
