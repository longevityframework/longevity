package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.model.ptype.Key
import longevity.model.realized.RealizedKey

/** implementation of CassandraPRepo.retrieve(KeyVal) */
private[cassandra] trait CassandraRetrieve[F[_], M, P] {
  repo: CassandraPRepo[F, M, P] =>

  def retrieve[V : Key[M, P, ?]](v: V) = {
    val fv = effect.pure(v)
    val fv2 = effect.map(fv) { v =>
      logger.debug(s"executing CassandraPRepo.retrieve: $v")
      v
    }
    val fso = effect.mapBlocking(fv2) { v =>
      retrieveFromBoundStatement(bindKeyValSelectStatement(v))
    }
    effect.map(fso) { stateOption =>
      logger.debug(s"done executing CassandraPRepo.retrieve: $stateOption")
      stateOption
    }
  }

  protected def retrieveFromBoundStatement(statement: BoundStatement) = {
    val resultSet = session().execute(statement)
    val rowOption = Option(resultSet.one)
    rowOption.map(retrieveFromRow(_, false))
  }

  private def bindKeyValSelectStatement[V : Key[M, P, ?]](keyVal: V): BoundStatement = {
    val tk = implicitly[Key[M, P, V]].keyValTypeKey
    val realizedKey: RealizedKey[M, P, V] = realizedPType.realizedKey(tk)
    val propVals = realizedKey.realizedProp.realizedPropComponents.map { component =>
      cassandraValue(component.innerPropPath.get(keyVal))
    }
    val statement = keyValSelectStatement(realizedKey)
    logger.debug(s"invoking CQL: ${statement.getQueryString} with bindings: $propVals")
    statement.bind(propVals: _*)
  }

  private def keyValSelectStatement(key: RealizedKey[M, P, _]): PreparedStatement = {
    val conjunction = keyValSelectStatementConjunction(key)
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction
    |ALLOW FILTERING
    |""".stripMargin
    preparedStatement(cql)
  }

  protected def keyValSelectStatementConjunction(key: RealizedKey[M, P, _]): String = {
    key.realizedProp.realizedPropComponents.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
  }

}
