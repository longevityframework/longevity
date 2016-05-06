package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence._ // TODO
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.create */
private[cassandra] trait CassandraCreate[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override def create(p: P)(implicit context: ExecutionContext) = Future {
    val uuid = UUID.randomUUID
    blocking {
      session.execute(bindInsertStatement(uuid, p))
    }
    new PState[P](CassandraId(uuid), p)
  }
  
  private lazy val insertStatement: PreparedStatement = {
    val columns = insertColumnNames.mkString(",\n  ")
    val substitutionPatterns = insertColumnNames.map(c => s":$c").mkString(",\n  ")

    val cql = s"""|
    |INSERT INTO $tableName (
    |  $columns
    |) VALUES (
    |  $substitutionPatterns
    |)
    |""".stripMargin

    session.prepare(cql)
  }

  protected def insertColumnNames: Seq[String] = {
    val realizedPropColumnNames = realizedProps.map(columnName).toSeq.sorted
    "id" +: "p" +: realizedPropColumnNames
  }

  private def bindInsertStatement(uuid: UUID, p: P): BoundStatement = {
    insertStatement.bind(insertColumnValues(uuid, p).toArray: _*)
  }

  protected def insertColumnValues(uuid: UUID, p: P): Seq[AnyRef] = {
    val realizedPropValues = realizedProps.toSeq.sortBy(columnName).map { prop =>
      def bind[PP >: P <: Persistent](prop: Prop[PP, _]) = propValBinding(prop, p)
      bind(prop)
    }
    uuid +: jsonStringForP(p) +: realizedPropValues
  }

}
