package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of CassandraPRepo.delete */
private[cassandra] trait CassandraDelete[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  def delete(state: PState[P]): F[Deleted[P]] = {
    val fs = effect.pure(state)
    val fs2 = effect.map(fs) { s =>
      logger.debug(s"executing CassandraPRepo.delete: $s")
      validateStablePrimaryKey(s)
      s
    }
    val fsr = effect.mapBlocking(fs2) { s =>
      val r = session().execute(bindDeleteStatement(s))
      (s, r)
    }
    effect.map(fsr) { case (s, r) =>
      if (persistenceConfig.optimisticLocking) {
        val deleteSuccess = r.one.getBool(0)
        if (!deleteSuccess) {
          throw new WriteConflictException(s)
        }
      }
      val deleted = new Deleted(s.get)
      logger.debug(s"done executing CassandraPRepo.delete: $deleted")
      deleted
    }
  }

  private def bindDeleteStatement(state: PState[P]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val bindings = if (persistenceConfig.optimisticLocking) {
      whereBindings(state) :+ state.rowVersionOrNull
    } else {
      whereBindings(state)
    }
    logger.debug(s"invoking CQL: ${deleteStatement.getQueryString} with bindings $bindings")
    boundStatement.bind(bindings: _*)
  }

  protected[cassandra] def deleteStatement = deleteStatementPreparedOnce

  private lazy val deleteStatementPreparedOnce = preparedStatement(deleteStatementCql)

  private def deleteStatementCql: String = if (persistenceConfig.optimisticLocking) {
    s"""|
    |DELETE FROM $tableName
    |WHERE
    |  $whereAssignments
    |IF
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
