package longevity.persistence.cassandra

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.datastax.driver.core.ResultSet
import longevity.exceptions.persistence.cassandra.AllInQueryException
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.Query.All
import longevity.subdomain.ptype.Query.AndOp
import longevity.subdomain.ptype.Query.EqOp
import longevity.subdomain.ptype.Query.GtOp
import longevity.subdomain.ptype.Query.GteOp
import longevity.subdomain.ptype.Query.LtOp
import longevity.subdomain.ptype.Query.LteOp
import longevity.subdomain.ptype.Query.NeqOp
import longevity.subdomain.ptype.Query.OrOp
import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.retrieveByQuery */
private[cassandra] trait CassandraQuery[P <: Persistent] {
  repo: CassandraRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext): Future[Seq[PState[P]]] =
    Future {
      val resultSet = blocking {
        queryResultSet(query)
      }
      resultSet.all.toList.map(retrieveFromRow)
    }

  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed] = {
    def iterator(): Iterator[PState[P]] = {
      val resultSet = queryResultSet(query)
      import scala.collection.JavaConversions.asScalaIterator
      resultSet.iterator.map(retrieveFromRow)
    }
    // no need (or option) to clean up resources once stream terminates, because
    // Cassandra result set is paged, and does not support any close() operation
    Source.fromIterator(iterator)
  }

  private def queryResultSet(query: Query[P]): ResultSet = {
    val info = queryInfo(query)
    val conjunction = retrieveByQueryConjunction(info)
    val cql = s"SELECT * FROM $tableName WHERE $conjunction ALLOW FILTERING"
    val preparedStatement = session.prepare(cql)
    val boundStatement = preparedStatement.bind(info.bindValues: _*)
    session.execute(boundStatement)
  }

  protected def retrieveByQueryConjunction(queryInfo: QueryInfo): String = queryInfo.whereClause

  protected case class QueryInfo(whereClause: String, bindValues: Seq[AnyRef])

  private def queryInfo(query: Query[P]): QueryInfo = {
    query match {
      case All() => throw new AllInQueryException
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => QueryInfo(s"${columnName(prop)} = :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.propTypeKey)))
        case NeqOp => throw new NeqInQueryException
      }
      case OrderingQuery(prop, op, value) => op match {
        case LtOp => QueryInfo(s"${columnName(prop)} < :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.propTypeKey)))
        case LteOp => QueryInfo(s"${columnName(prop)} <= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.propTypeKey)))
        case GtOp => QueryInfo(s"${columnName(prop)} > :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.propTypeKey)))
        case GteOp => QueryInfo(s"${columnName(prop)} >= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.propTypeKey)))
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
