package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.Prop
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.retrieve(KeyVal) */
private[cassandra] trait CassandraRetrieveKeyVal[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override protected def retrieveByKeyVal(keyVal: KeyVal[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]] =
    retrieveFromBoundStatement(bindKeyValSelectStatement(keyVal))

  private lazy val keyValSelectStatement: Map[Key[P], PreparedStatement] = Map().withDefault { key =>
    val relations = key.props.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $relations
    |ALLOW FILTERING
    |""".stripMargin
    session.prepare(cql)
  }

  private def bindKeyValSelectStatement(keyVal: KeyVal[P]): BoundStatement = {
    val preparedStatement = keyValSelectStatement(keyVal.key)
    val propVals = keyVal.key.props.map { prop =>
      def bind[A](prop: Prop[P, A]) = cassandraValue(keyVal(prop))(prop.propTypeKey)
      bind(prop)
    }
    preparedStatement.bind(propVals: _*)
  }

}
