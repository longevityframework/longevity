package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import java.util.UUID
import longevity.effect.Effect.Syntax
import longevity.persistence.PState
import org.joda.time.DateTime

/** implementation of CassandraPRepo.create */
private[cassandra] trait CassandraCreate[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  override def create(p: P): F[PState[P]] = {
    val fp = effect.pure(p)
    val fs = effect.map(fp) { p =>
      logger.debug(s"executing CassandraPRepo.create: $p")
      val id = if (hasPrimaryKey) None else Some(CassandraId(UUID.randomUUID))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
      PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
    }
    val fs2 = effect.mapBlocking(fs)(createStateBlocking)
    effect.map(fs2) { s =>
      logger.debug(s"done executing CassandraPRepo.create: $s")
      s
    }
  }

  private[persistence] def createState(state: PState[P]): F[PState[P]] =
    effect.pure(state).mapBlocking(createStateBlocking)

  private def createStateBlocking(s: PState[P]): PState[P] = {
    session().execute(bindInsertStatement(s))
    s
  }

  private def bindInsertStatement(state: PState[P]): BoundStatement = {
    val bindings = updateColumnValues(state, isCreate = true)
    logger.debug(s"invoking CQL: $insertStatement with bindings: $bindings")
    insertStatement.bind(bindings: _*)
  }

  private def insertStatement: PreparedStatement = {
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
