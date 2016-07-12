package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.BasicPropComponent
import longevity.subdomain.realized.RealizedKey
import scala.concurrent.ExecutionContext

/** implementation of CassandraRepo.retrieve(KeyVal) */
private[cassandra] trait CassandraRetrieve[P <: Persistent] {
  repo: CassandraRepo[P] =>

  def retrieve[V <: KeyVal[P, V]](keyVal: V)(implicit context: ExecutionContext) =
    retrieveFromBoundStatement(bindKeyValSelectStatement(keyVal))

  private lazy val keyValSelectStatement: Map[RealizedKey[P, _], PreparedStatement] = Map().withDefault { key =>
    val conjunction = keyValSelectStatementConjunction(key)
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction
    |ALLOW FILTERING
    |""".stripMargin
    session.prepare(cql)
  }

  protected def keyValSelectStatementConjunction(key: RealizedKey[P, _]): String = {
    key.realizedProp.basicPropComponents.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
  }

  private def bindKeyValSelectStatement[V <: KeyVal[P, V]](keyVal: V): BoundStatement = {
    val realizedKey: RealizedKey[P, V] = realizedPType.realizedKeys(keyVal.key)
    val propVals = realizedKey.realizedProp.basicPropComponents.map { component =>
      def bind[PP >: P <: Persistent, B](component: BasicPropComponent[PP, V, B]) =
        cassandraValue(component.innerPropPath.get(keyVal), component)(component.componentTypeKey)
      bind(component)
    }
    val preparedStatement = keyValSelectStatement(realizedKey)
    preparedStatement.bind(propVals: _*)
  }

}
