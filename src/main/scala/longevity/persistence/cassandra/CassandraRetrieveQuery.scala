package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import java.util.UUID
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.subdomain.root.Query._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of CassandraRepo.retrieveByQuery */
private[cassandra] trait CassandraRetrieveQuery[R <: Root] {
  repo: CassandraRepo[R] =>

  def retrieveByQuery(query: Query[R])(implicit context: ExecutionContext): Future[Seq[PState[R]]] =
    Future {
      val info = queryInfo(query)
      val cql = s"SELECT * FROM $tableName WHERE ${info.whereClause} ALLOW FILTERING"
      val preparedStatement = session.prepare(cql)
      val boundStatement = preparedStatement.bind(info.bindValues: _*)
      val resultSet = session.execute(boundStatement)
      resultSet.all.toList.map(retrieveFromRow)
    }

  private case class QueryInfo(whereClause: String, bindValues: Seq[AnyRef])

  private def queryInfo(query: Query[R]): QueryInfo = {
    query match {
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => QueryInfo(s"${columnName(prop)} = :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case NeqOp => throw new NeqInQueryException
      }
      case OrderingQuery(prop, op, value) => op match {
        case LtOp => QueryInfo(s"${columnName(prop)} < :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case LteOp => QueryInfo(s"${columnName(prop)} <= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.typeKey)))
        case GtOp => QueryInfo(s"${columnName(prop)} > :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case GteOp => QueryInfo(s"${columnName(prop)} >= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.typeKey)))
      }
      case ConditionalQuery(lhs, op, rhs) => op match {
        case AndOp =>
          val lhsQueryInfo = queryInfo(lhs)
          val rhsQueryInfo = queryInfo(rhs)
          QueryInfo(s"${lhsQueryInfo.whereClause} AND ${rhsQueryInfo.whereClause}",
                    lhsQueryInfo.bindValues ++ rhsQueryInfo.bindValues)
        case OrOp => throw new OrInQueryException
      }
    }
  }

}
