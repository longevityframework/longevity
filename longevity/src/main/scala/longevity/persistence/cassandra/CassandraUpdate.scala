package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState

/** implementation of CassandraPRepo.update */
private[cassandra] trait CassandraUpdate[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  def update(state: PState[P]): F[PState[P]] = {
    val fs = effect.pure(state)
    val fss = effect.map(fs) { s =>
      logger.debug(s"executing CassandraPRepo.update: $s")
      validateStablePrimaryKey(s)
      (s, s.update(persistenceConfig.optimisticLocking, persistenceConfig.writeTimestamps))
    }
    val fsr = effect.mapBlocking(fss) { case (oldState, newState) =>
      val resultSet = session().execute(bindUpdateStatement(newState, oldState.rowVersionOrNull))
      (newState, resultSet)
    }
    effect.map(fsr) { case (s, r) =>
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

  private lazy val updateStatement = preparedStatement(updateCql)

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

}
