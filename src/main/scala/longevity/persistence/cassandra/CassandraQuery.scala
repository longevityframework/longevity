package longevity.persistence.cassandra

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.datastax.driver.core.ResultSet
import longevity.exceptions.persistence.cassandra.AllInQueryException
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.Prop
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
import longevity.subdomain.realized.BasicPropComponent
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

  private object QueryInfo {
    def and(lhs: QueryInfo, rhs: QueryInfo) =
      QueryInfo(s"${lhs.whereClause} AND ${rhs.whereClause}", lhs.bindValues ++ rhs.bindValues)    
  }

  private def queryInfo(query: Query[P]): QueryInfo = query match {
    case All() => throw new AllInQueryException
    case q: EqualityQuery[_, _] => equalityQueryQueryInfo(q)
    case q: OrderingQuery[_, _] => orderingQueryQueryInfo(q)
    case ConditionalQuery(lhs, op, rhs) => op match {
      case AndOp =>
        QueryInfo.and(queryInfo(lhs), queryInfo(rhs))
      case OrOp => throw new OrInQueryException
    }
  }

  private def equalityQueryQueryInfo[A](query: EqualityQuery[P, A]): QueryInfo = query.op match {
    case EqOp =>
      val infos: Seq[QueryInfo] = toComponents(query.prop).map { component =>
        def info[B](component: BasicPropComponent[_ >: P <: Persistent, A, B]) = {
          val componentValue =
            cassandraValue[B](component.innerPropPath.get(query.value), component)(component.componentTypeKey)
          QueryInfo(s"${columnName(component)} = :${columnName(component)}",
                    Seq(componentValue))
        }
        info(component)
      }
      infos.tail.fold(infos.head)(QueryInfo.and)
    case NeqOp => throw new NeqInQueryException
  }

  private def orderingQueryQueryInfo[A](query: OrderingQuery[P, A]): QueryInfo = {
    val components = toComponents(query.prop)
    def componentsToQueryInfo(components: Seq[BasicPropComponent[_ >: P <: Persistent, A, _]]): QueryInfo = {
      if (components.size == 1) {
        def info[B](component: BasicPropComponent[_ >: P <: Persistent, A, B]) = {
          val componentValue = cassandraValue[B](
            component.innerPropPath.get(query.value),
            component)(
            component.componentTypeKey)
          val opString = query.op match {
            case LtOp => "<"
            case LteOp => "<="
            case GtOp => ">"
            case GteOp => ">="
          }
          QueryInfo(s"${columnName(component)} $opString :${columnName(component)}", Seq(componentValue))
        }
        info(components.head)
      } else {
        throw new CompoundPropInOrderingQuery
      }
    }
    componentsToQueryInfo(components)
  }

  def toComponents[A](prop: Prop[_ >: P <: Persistent, A])
  : Seq[BasicPropComponent[_ >: P <: Persistent, A, _]] = {
    realizedPType.realizedProps(prop).basicPropComponents
  }


}
