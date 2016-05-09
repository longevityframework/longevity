package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
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
    val columns = updateColumnNames(includeId = true).mkString(",\n  ")
    val substitutionPatterns = updateColumnNames(includeId = true).map(c => s":$c").mkString(",\n  ")

    val cql = s"""|
    |INSERT INTO $tableName (
    |  $columns
    |) VALUES (
    |  $substitutionPatterns
    |)
    |""".stripMargin

    session.prepare(cql)
  }

  private def bindInsertStatement(uuid: UUID, p: P): BoundStatement = {
    val bindings = updateColumnValues(uuid, p, includeId = true)
    insertStatement.bind(bindings: _*)
  }

}
