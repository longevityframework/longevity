package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import emblem.TypeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.realized.RealizedKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.retrieve(KeyVal) */
private[cassandra] trait CassandraRetrieve[P <: Persistent] {
  repo: CassandraRepo[P] =>

  def retrieve[V <: KeyVal[P, V] : TypeKey](keyVal: V)(implicit context: ExecutionContext) = {
    logger.debug(s"calling CassandraRepo.retrieve: $keyVal")
    val stateOption = retrieveFromBoundStatement(bindKeyValSelectStatement(keyVal))
    logger.debug(s"done calling CassandraRepo.retrieve: $stateOption")
    stateOption
  }

  protected def retrieveFromBoundStatement(statement: BoundStatement)(implicit context: ExecutionContext) =
    Future {
      val resultSet = blocking { session.execute(statement) }
      val rowOption = Option(resultSet.one)
      rowOption.map(retrieveFromRow)
    }

  private def bindKeyValSelectStatement[V <: KeyVal[P, V] : TypeKey](keyVal: V): BoundStatement = {
    val realizedKey: RealizedKey[P, V] = realizedPType.realizedKey[V]
    val propVals = realizedKey.realizedProp.realizedPropComponents.map { component =>
      cassandraValue(component.innerPropPath.get(keyVal))
    }
    val preparedStatement = keyValSelectStatement(realizedKey)
    logger.debug(s"invoking CQL: ${preparedStatement.getQueryString} with bindings: $propVals")
    preparedStatement.bind(propVals: _*)
  }

  private lazy val keyValSelectStatement: Map[RealizedKey[P, _], PreparedStatement] = Map().withDefault { key =>
    val conjunction = keyValSelectStatementConjunction(key)
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction
    |ALLOW FILTERING
    |""".stripMargin
    preparedStatement(cql)
  }

  protected def keyValSelectStatementConjunction(key: RealizedKey[P, _]): String = {
    key.realizedProp.realizedPropComponents.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
  }

}
