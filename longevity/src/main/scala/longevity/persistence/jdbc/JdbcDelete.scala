package longevity.persistence.jdbc

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of JdbcPRepo.delete */
private[jdbc] trait JdbcDelete[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  override def delete(state: PState[P]): F[Deleted[P]] = {
    val fs = effect.pure(state)
    val fs2 = effect.map(fs) { s =>
      logger.debug(s"calling JdbcPRepo.delete: $s")
      validateStablePrimaryKey(s)
      s
    }
    val fsr = effect.mapBlocking(fs2) { state =>
      val rowCount = bindDeleteStatement(state).executeUpdate()
      (state, rowCount)
    }
    effect.map(fsr) { case (state, rowCount) =>
      if (persistenceConfig.optimisticLocking && rowCount != 1) {
        throw new WriteConflictException(state)
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling JdbcPRepo.delete: $deleted")
      deleted
    }
  }

  private def bindDeleteStatement(state: PState[P]) = {
    val preparedStatement = connection().prepareStatement(deleteStatementSql)
    val bindings = if (persistenceConfig.optimisticLocking) {
      whereBindings(state) :+ state.rowVersionOrNull
    } else {
      whereBindings(state)
    }
    logger.debug(s"invoking SQL: $deleteStatementSql with bindings $bindings")
    bindings.zipWithIndex.foreach { case (binding, index) =>
      preparedStatement.setObject(index + 1, binding)
    }
    preparedStatement
  }

  private def deleteStatementSql: String = if (persistenceConfig.optimisticLocking) {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |AND
    |  row_version = :row_version
    |""".stripMargin
  } else {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |""".stripMargin
  }

}
