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
    val id = if (hasPartitionKey) None else Some(CassandraId[P](UUID.randomUUID))
    val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
    val state = PState(id, rowVersion, p)
    blocking {
      session.execute(bindInsertStatement(state))
    }
    logger.debug(s"done calling CassandraRepo.create: $state")
    state
  }

  private def bindInsertStatement(state: PState[P]): BoundStatement = {
    val bindings = updateColumnValues(state, isCreate = true)
    logger.debug(s"invoking CQL: $insertStatement with bindings: $bindings")
    insertStatement.bind(bindings: _*)
  }

  private lazy val insertStatement: PreparedStatement = {
    val names = updateColumnNames(isCreate = true)
    val columns = names.mkString(",\n  ")
    val substitutionPatterns = names.map(c => s":$c").mkString(",\n  ")

    val cql = s"""|
    |INSERT INTO $tableName (
    |  $columns
    |) VALUES (
    |  $substitutionPatterns
    |)
    |""".stripMargin

    preparedStatement(cql)
  }

}
