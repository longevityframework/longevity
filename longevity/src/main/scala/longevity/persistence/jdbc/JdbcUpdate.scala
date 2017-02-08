package longevity.persistence.jdbc

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of JdbcRepo.update */
private[jdbc] trait JdbcUpdate[P] {
  repo: JdbcRepo[P] =>

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] =
    Future {
      logger.debug(s"calling JdbcRepo.update: $state")
      validateStablePrimaryKey(state)
      val newState = state.update(persistenceConfig.optimisticLocking)
      val rowCount = blocking {
        try {
          bindUpdateStatement(newState, state.rowVersionOrNull).executeUpdate()
        } catch convertDuplicateKeyException(newState)
      }
      if (persistenceConfig.optimisticLocking && rowCount != 1) {
        throw new WriteConflictException(state)
      }
      logger.debug(s"done calling JdbcRepo.update: $newState")
      newState
    }

  private def bindUpdateStatement(state: PState[P], rowVersion: AnyRef) = {
    val columnBindings = if (persistenceConfig.optimisticLocking) {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state) :+ rowVersion
    } else {
      updateColumnValues(state, isCreate = false) ++: whereBindings(state)
    }
    logger.debug(s"invoking SQL: $updateSql with bindings: $columnBindings")
    val preparedStatement = connection.prepareStatement(updateSql)
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

}
