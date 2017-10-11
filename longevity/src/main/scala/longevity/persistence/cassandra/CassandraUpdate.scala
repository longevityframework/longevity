package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.effect.Effect.Syntax
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState

/** implementation of CassandraPRepo.update */
private[cassandra] trait CassandraUpdate[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  def update(state: PState[P]): F[PState[P]] = {
    val fs = effect.pure(state)
    val fss = fs.map { s =>
      logger.debug(s"executing CassandraPRepo.update: $s")
      validateStablePrimaryKey(s)
      (s, s.update(persistenceConfig.optimisticLocking, persistenceConfig.writeTimestamps))
    }
    val fsr = fss.mapBlocking { case (oldState, newState) =>
      val resultSet = session().execute(bindUpdateStatement(newState, oldState.rowVersionOrNull))
      (newState, resultSet)
    }
    fsr.map { case (s, r) =>
      if (persistenceConfig.optimisticLocking) {
        val updateSuccess = r.one.getBool(0)
        if (!updateSuccess) {
          throw new WriteConflictException(s)
        }
      }
      logger.debug(s"done executing CassandraPRepo.update: $s")
      s
    }
  }

  private def bindUpdateStatement(state: PState[P], rowVersion: AnyRef): BoundStatement = {
    val columnBindings = if (persistenceConfig.optimisticLocking) {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state) :+ rowVersion
    } else {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state)
    }
    logger.debug(s"invoking CQL: ${updateStatement.getQueryString} with bindings: $columnBindings")
    updateStatement.bind(columnBindings: _*)
  }

  private def updateStatement = preparedStatement(updateCql)

  private def updateCql = if (persistenceConfig.optimisticLocking) {
    withLockingUpdateCql
  } else {
    withoutLockingUpdateCql
  }

  private def columnAssignments = updateColumnNames(isCreate = false).map(c => s"$c = :$c").mkString(",\n  ")

  private def withoutLockingUpdateCql = s"""|
  |UPDATE $tableName
  |SET
  |  $columnAssignments
  |WHERE
  |  $whereAssignments
  |""".stripMargin

  private def withLockingUpdateCql = s"""|$withoutLockingUpdateCql
  |IF
  |  row_version = :row_version
  |""".stripMargin

  private lazy val updateMigrationStartedCql =
    s"UPDATE $tableName SET migration_started = true WHERE $whereAssignments"

  private def updateMigrationStartedStatement = preparedStatement(updateMigrationStartedCql)

  protected[persistence] def updateMigrationStarted(state: PState[P]): F[Unit] =
    effect.pure(state).mapBlocking { s =>
      session().execute(updateMigrationStartedStatement.bind(whereBindings(s): _*))
      ()
    }

  private lazy val updateMigrationCompleteCql =
    s"UPDATE $tableName SET migration_complete = true WHERE $whereAssignments"

  private def updateMigrationCompleteStatement = preparedStatement(updateMigrationCompleteCql)

  protected[persistence] def updateMigrationComplete(state: PState[P]): F[Unit] =
    effect.pure(state).mapBlocking { s =>
      session().execute(updateMigrationCompleteStatement.bind(whereBindings(s): _*))
      ()
    }
  
}
