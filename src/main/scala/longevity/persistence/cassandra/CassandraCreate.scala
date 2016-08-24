package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import java.util.UUID
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.create */
private[cassandra] trait CassandraCreate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    val uuid = UUID.randomUUID
    val modifiedDate = persistenceConfig.modifiedDate
    blocking {
      session.execute(bindInsertStatement(uuid, modifiedDate, p))
    }
    PState(CassandraId[P](uuid), modifiedDate, p)
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

  private def bindInsertStatement(uuid: UUID, modifiedDate: Option[DateTime], p: P): BoundStatement = {
    val bindings = updateColumnValues(uuid, modifiedDate, p, includeId = true)
    insertStatement.bind(bindings: _*)
  }

}
