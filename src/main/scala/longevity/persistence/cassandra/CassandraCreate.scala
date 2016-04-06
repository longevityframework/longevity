package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence._
import longevity.subdomain.persistent.Persistent
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.create */
private[cassandra] trait CassandraCreate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    val uuid = UUID.randomUUID
    blocking {
      session.execute(bindInsertStatement(uuid, p))
    }
    new PState[P](CassandraId(uuid), p)
  }
  
  private lazy val insertStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"INSERT INTO $tableName (id, p) VALUES (:id, :p)"
    } else {
      val realizedPropColumnNames = realizedProps.map(columnName).toSeq.sorted
      val realizedPropColumns = realizedPropColumnNames.mkString(",\n  ")
      val realizedSubstitutions = realizedPropColumnNames.map(c => s":$c").mkString(",\n  ")
      s"""|
      |INSERT INTO $tableName (
      |  id,
      |  p,
      |  $realizedPropColumns
      |) VALUES (
      |  :id,
      |  :p,
      |  $realizedSubstitutions
      |)
      |""".stripMargin
    }
    session.prepare(cql)
  }

  private def bindInsertStatement(uuid: UUID, p: P): BoundStatement = {
    val nonPropValues = Array(uuid, jsonStringForP(p))
    val realizedPropValues = realizedProps.toSeq.sortBy(columnName).map(propValBinding(_, p))
    val values = (nonPropValues ++ realizedPropValues)
    insertStatement.bind(values: _*)
  }

}
