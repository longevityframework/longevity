package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.Persistent
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.delete */
private[cassandra] trait CassandraDelete[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] = Future {
    blocking {
      logger.debug(s"calling CassandraRepo.delete: $state")
      validateStablePartitionKey(state)
      val resultSet = session.execute(bindDeleteStatement(state))
      if (persistenceConfig.optimisticLocking) {
        val deleteSuccess = resultSet.one.getBool(0)
        if (!deleteSuccess) {
          throw new WriteConflictException(state)
        }
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling CassandraRepo.delete: $deleted")
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

  // TODO duplicated in CassandraUpdate
  private def whereAssignments = if (hasPartitionKey) {
    partitionKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  // TODO duplicated in CassandraUpdate
  private def whereBindings(state: PState[P]) = if (hasPartitionKey) {
    partitionKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[CassandraId[P]].uuid)
  }

}
