package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence._
import longevity.subdomain._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** implementation of CassandraRepo.create */
private[cassandra] trait CassandraCreate[R <: Root] {
  repo: CassandraRepo[R] =>

  override def create(unpersisted: R) = Future {
    val uuid = UUID.randomUUID
    session.execute(bindInsertStatement(uuid, unpersisted))
    new PState[R](CassandraId(uuid, repo.rootTypeKey), unpersisted)
  }
  
  private val insertStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"INSERT INTO $tableName (id, root) VALUES (:id, :root)"
    } else {
      val realizedPropColumnNames = realizedProps.map(columnName).toSeq.sorted
      val realizedPropColumns = realizedPropColumnNames.mkString(",\n  ")
      val realizedSubstitutions = realizedPropColumnNames.map(c => s":$c").mkString(",\n  ")
      s"""|
      |INSERT INTO $tableName (
      |  id,
      |  root,
      |  $realizedPropColumns
      |) VALUES (
      |  :id,
      |  :root,
      |  $realizedSubstitutions
      |)
      |""".stripMargin
    }
    session.prepare(cql)
  }

  private def bindInsertStatement(uuid: UUID, root: R): BoundStatement = {
    val nonPropValues = Array(uuid, jsonStringForRoot(root))
    val realizedPropValues = realizedProps.toSeq.sortBy(columnName).map(propValBinding(_, root))
    val values = (nonPropValues ++ realizedPropValues)
    insertStatement.bind(values: _*)
  }

}
