package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.update */
private[cassandra] trait CassandraUpdate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] =
    Future {
      blocking {
        session.execute(bindUpdateStatement(state))
      }
      new PState[P](state.passoc, state.get)
    }

  private lazy val updateStatement: PreparedStatement = {
    val columnAssignments = updateColumnNames(includeId = false).map(c => s"$c = :$c").mkString(",\n  ")

    val cql = s"""|
    |UPDATE $tableName
    |SET
    |  $columnAssignments
    |WHERE
    |  id = :id
    |""".stripMargin

    session.prepare(cql)
  }

  private def bindUpdateStatement(state: PState[P]): BoundStatement = {
    val uuid = state.passoc.asInstanceOf[CassandraId[P]].uuid
    val p = state.get
    val columnBindings = updateColumnValues(uuid, p, includeId = false) :+ uuid
    updateStatement.bind(columnBindings: _*)
  }

}
