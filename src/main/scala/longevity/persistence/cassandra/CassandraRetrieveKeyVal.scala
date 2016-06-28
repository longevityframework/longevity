package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key
import longevity.subdomain.realized.BasicPropComponent
import longevity.subdomain.realized.RealizedKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

// TODO rename to CassandraRetrieve

/** implementation of CassandraRepo.retrieve(KeyVal) */
private[cassandra] trait CassandraRetrieveKeyVal[P <: Persistent] {
  repo: CassandraRepo[P] =>

  override protected def retrieveByKeyVal(keyVal: KeyVal[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]] =
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

  private def bindKeyValSelectStatement(keyVal: KeyVal[P]): BoundStatement = {
    def boundStatement[KV <: KeyVal[P]](keyVal: KV) = {
      // TODO: we should be able to get rid of this asInstanceOf if KeyVal.key was better typed
      val realizedKey: RealizedKey[P, KV] = realizedPType.realizedKeys(keyVal.key.asInstanceOf[Key[P, KV]])
      val propVals = realizedKey.realizedProp.basicPropComponents.map { component =>
        def bind[PP >: P <: Persistent, B](component: BasicPropComponent[PP, KV, B]) =
          cassandraValue(component.innerPropPath.get(keyVal), component)(component.componentTypeKey)
        bind(component)
      }
      val preparedStatement = keyValSelectStatement(realizedKey)
      preparedStatement.bind(propVals: _*)
    }
    boundStatement(keyVal)
  }

}
