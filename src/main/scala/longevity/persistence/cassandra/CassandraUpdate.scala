package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.update */
private[cassandra] trait CassandraUpdate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] =
    Future {
      val modifiedDate = persistenceConfig.modifiedDate
      val resultSet = blocking {
        session.execute(bindUpdateStatement(state, modifiedDate))
      }
      val updateSuccess = resultSet.one.getBool(0)
      if (!updateSuccess) {
        throw new WriteConflictException(state)
      }
      PState[P](state.id, modifiedDate, state.get)
    }

  private lazy val updateStatement: PreparedStatement = {
    session.prepare(updateCql)
  }

  private def updateCql = {
    val columnAssignments = updateColumnNames(includeId = false).map(c => s"$c = :$c").mkString(",\n  ")
    val noLockCql = s"""|
    |UPDATE $tableName
    |SET
    |  $columnAssignments
    |WHERE
    |  id = :id
    |""".stripMargin

    if (persistenceConfig.optimisticLocking) {
      s"""|
      |$noLockCql
      |IF
      |  modified_date = :modified_date
      |""".stripMargin
    } else {
      noLockCql
    }
  }

  private def bindUpdateStatement(state: PState[P], modifiedDate: Option[DateTime]): BoundStatement = {
    val uuid = state.id.asInstanceOf[CassandraId[P]].uuid
    val p = state.get
    def dateCheck = state.modifiedDate.map(cassandraDate).orNull
    val columnBindings = if (persistenceConfig.optimisticLocking) {
      updateColumnValues(uuid, modifiedDate, p, includeId = false) :+ uuid :+ dateCheck
    } else {
      updateColumnValues(uuid, modifiedDate, p, includeId = false) :+ uuid
    }
    updateStatement.bind(columnBindings: _*)
  }

}
