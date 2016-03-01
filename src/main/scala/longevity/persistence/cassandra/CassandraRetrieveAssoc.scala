package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.persistence._
import longevity.subdomain._
import scala.concurrent.Future

/** implementation of CassandraRepo.retrievePersistedAssoc */
private[cassandra] trait CassandraRetrieveAssoc[R <: Root] {
  repo: CassandraRepo[R] =>

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] =
    retrieveFromBoundStatement(bindIdSelectStatement(assoc.asInstanceOf[CassandraId[R]]))

  private lazy val idSelectStatement = {
    val cql = s"SELECT * FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindIdSelectStatement(assoc: CassandraId[R]): BoundStatement = {
    idSelectStatement.bind(assoc.uuid)
  }

}
