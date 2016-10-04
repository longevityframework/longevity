package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import java.util.UUID
import longevity.persistence.PState
import longevity.subdomain.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.create */
private[cassandra] trait CassandraCreate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    logger.debug(s"calling CassandraRepo.create: $p")
    val uuid = UUID.randomUUID
    val rowVersion = Some(0L)
    blocking {
      session.execute(bindInsertStatement(uuid, rowVersion, p))
    }
    val state = PState(CassandraId[P](uuid), rowVersion, p)
    logger.debug(s"done calling CassandraRepo.create: $state")
    state
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

    preparedStatement(cql)
  }

  private def bindInsertStatement(uuid: UUID, rowVersion: Option[Long], p: P): BoundStatement = {
    val bindings = updateColumnValues(uuid, rowVersion, p, includeId = true)
    logger.debug(s"invoking CQL: $insertStatement with bindings: $bindings")
    insertStatement.bind(bindings: _*)
  }

}
