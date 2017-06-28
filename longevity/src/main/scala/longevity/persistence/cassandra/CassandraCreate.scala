package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import java.util.UUID
import longevity.persistence.PState
import org.joda.time.DateTime

/** implementation of CassandraPRepo.create */
private[cassandra] trait CassandraCreate[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  override def create(p: P): F[PState[P]] = effect.mapBlocking(effect.pure(p)) { p =>
    logger.debug(s"calling CassandraPRepo.create: $p")
    val id = if (hasPrimaryKey) None else Some(CassandraId[P](UUID.randomUUID))
    val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
    val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
    val s = PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
    session().execute(bindInsertStatement(s))
    logger.debug(s"done calling CassandraPRepo.create: $s")
    s
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
