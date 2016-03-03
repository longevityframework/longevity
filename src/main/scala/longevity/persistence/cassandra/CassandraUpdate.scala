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
private[cassandra] trait CassandraUpdate[R <: Root] {
  repo: CassandraRepo[R] =>

  override def update(state: PState[R])(implicit context: ExecutionContext): Future[PState[R]] =
    Future {
      session.execute(bindUpdateStatement(state))
      new PState[R](state.passoc, state.get)
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

  private def bindUpdateStatement(state: PState[R]): BoundStatement = {
    val root = state.get
    val json = jsonStringForRoot(root)
    val realizedPropVals = realizedProps.toArray.sortBy(columnName).map(propValBinding(_, root))
    val uuid = state.assoc.asInstanceOf[CassandraId[R]].uuid
    val values = (json +: realizedPropVals :+ uuid)
    updateStatement.bind(values: _*)
  }

}
