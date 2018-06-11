package longevity.persistence.jdbc

import longevity.effect.Effect.Syntax
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState

/** implementation of JdbcPRepo.update */
private[jdbc] trait JdbcUpdate[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  override def update(state: PState[P]): F[PState[P]] = {
    val fss = effect.pure(state).map { state =>
      logger.debug(s"executing JdbcPRepo.update: $state")
      validateStablePrimaryKey(state)
      val newState = state.update(persistenceConfig.optimisticLocking, persistenceConfig.writeTimestamps)
      (state, newState)
    }
    val fssr = fss.mapBlocking { case (state, newState) =>
      val rowCount = try {
        bindUpdateStatement(newState, state.rowVersionOrNull).executeUpdate()
      } catch {
        convertDuplicateKeyException(newState)
      }
      (state, newState, rowCount)
    }
    fssr.map { case (state, newState, rowCount) =>
      if (persistenceConfig.optimisticLocking && rowCount != 1) {
        throw new WriteConflictException(state)
      }
      logger.debug(s"done executing JdbcPRepo.update: $newState")
      newState
    }
  }

  private def bindUpdateStatement(state: PState[P], rowVersion: AnyRef) = {
    val columnBindings = if (persistenceConfig.optimisticLocking) {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state) :+ rowVersion
    } else {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state)
    }
    logger.debug(s"invoking SQL: $updateSql with bindings: $columnBindings")
    val preparedStatement = connection().prepareStatement(updateSql)
    columnBindings.zipWithIndex.foreach { case (binding, index) =>
      preparedStatement.setObject(index + 1, binding)
    }
    preparedStatement
  }

  private def updateSql = if (persistenceConfig.optimisticLocking) {
    withLockingUpdateSql
  } else {
    withoutLockingUpdateSql
  }

  private def columnAssignments = updateColumnNames(isCreate = false).map(c => s"$c = :$c").mkString(",\n  ")

  private def withoutLockingUpdateSql = s"""|
  |UPDATE $tableName
  |SET
  |  $columnAssignments
  |WHERE
  |  $whereAssignments
  |""".stripMargin

  private def withLockingUpdateSql = s"""|$withoutLockingUpdateSql
  |AND
  |  row_version = :old_row_version
  |""".stripMargin

  private lazy val updateMigrationStartedSql =
    s"""UPDATE $tableName SET migration_started = 1 WHERE $whereAssignments"""

  protected[persistence] def updateMigrationStarted(state: PState[P]): F[Unit] = {
    effect.pure(state).mapBlocking { state =>
      val statement = connection().prepareStatement(updateMigrationStartedSql)
      whereBindings(state).zipWithIndex.foreach { case (binding, index) =>
        statement.setObject(index + 1, binding)
      }
      statement.executeUpdate()
    }
  }

  private lazy val updateMigrationCompleteSql =
    s"""UPDATE $tableName SET migration_complete = 1 WHERE $whereAssignments"""

  protected[persistence] def updateMigrationComplete(state: PState[P]): F[Unit] = {
    effect.pure(state).mapBlocking { state =>
      val statement = connection().prepareStatement(updateMigrationCompleteSql)
      whereBindings(state).zipWithIndex.foreach { case (binding, index) =>
        statement.setObject(index + 1, binding)
      }
      statement.executeUpdate()
    }
  }

}
