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
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.update */
private[cassandra] trait CassandraUpdate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] =
    Future {
      session.execute(bindUpdateStatement(state))
      new PState[P](state.passoc, state.get)
    }

  private lazy val updateStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"UPDATE $tableName SET root = :root WHERE id = :id"
    } else {
      val realizedPropColumnNames = realizedProps.toSeq.map(columnName).sorted
      val realizedAssignments = realizedPropColumnNames.map(c => s"$c = :$c").mkString(",\n  ")
      s"""|
      |UPDATE $tableName
      |SET
      |  root = :root,
      |  $realizedAssignments
      |WHERE
      |  id = :id
      |""".stripMargin
    }
    session.prepare(cql)
  }

  private def bindUpdateStatement(state: PState[P]): BoundStatement = {
    val p = state.get
    val json = jsonStringForRoot(p)
    val realizedPropVals = realizedProps.toArray.sortBy(columnName).map(propValBinding(_, p))
    val uuid = state.assoc.asInstanceOf[CassandraId[P]].uuid
    val values = (json +: realizedPropVals :+ uuid)
    updateStatement.bind(values: _*)
  }

}
