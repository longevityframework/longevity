package longevity.persistence.jdbc

import java.sql.PreparedStatement
import longevity.model.ptype.Key
import longevity.model.realized.RealizedKey

/** implementation of JdbcPRepo.retrieve(KeyVal) */
private[jdbc] trait JdbcRetrieve[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  override def retrieve[V : Key[M, P, ?]](keyVal: V) = {
    val fv = effect.pure(keyVal)
    val fv2 = effect.map(fv) { v =>
      logger.debug(s"executing JdbcPRepo.retrieve: $v")
      v
    }
    val fso = effect.mapBlocking(fv2) { v =>
      retrieveFromPreparedStatement(bindKeyValSelectStatement(v))
    }
    effect.map(fso) { so =>
      logger.debug(s"done executing JdbcPRepo.retrieve: $so")
      so
    }
  }

  protected def retrieveFromPreparedStatement(statement: PreparedStatement) = {
    val resultSet = connection.executeQuery(statement)
    if (resultSet.next()) {
      Some(retrieveFromResultSet(resultSet))
    } else {
      None
    }
  }

  private def bindKeyValSelectStatement[V : Key[M, P, ?]](keyVal: V) = {
    val realizedKey: RealizedKey[M, P, V] = realizedPType.realizedKey(implicitly[Key[M, P, V]].keyValTypeKey)
    val propVals = realizedKey.realizedProp.realizedPropComponents.map { component =>
      jdbcValue(component.innerPropPath.get(keyVal))
    }
    val sql = keyValSelectStatement(realizedKey)
    val preparedStatement = connection.prepareStatement(sql)
    logger.debug(s"invoking SQL: $sql with bindings: $propVals")
    propVals.zipWithIndex.foreach { case (propVal, index) =>
      preparedStatement.setObject(index + 1, propVal)
    }
    preparedStatement
  }

  private lazy val keyValSelectStatement: Map[RealizedKey[M, P, _], String] = Map().withDefault { key =>
    val conjunction = keyValSelectStatementConjunction(key)
    s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction
    |""".stripMargin
  }

  protected def keyValSelectStatementConjunction(key: RealizedKey[M, P, _]): String = {
    key.realizedProp.realizedPropComponents.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
  }

}
