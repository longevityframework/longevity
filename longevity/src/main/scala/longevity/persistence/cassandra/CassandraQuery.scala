package longevity.persistence.cassandra

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.datastax.driver.core.ResultSet
import longevity.exceptions.persistence.cassandra.FilterAllInQueryException
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OffsetInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.exceptions.persistence.cassandra.OrderByInQueryException
import longevity.persistence.PState
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Prop
import longevity.subdomain.query.AndOp
import longevity.subdomain.query.ConditionalFilter
import longevity.subdomain.query.EqOp
import longevity.subdomain.query.GtOp
import longevity.subdomain.query.GteOp
import longevity.subdomain.query.LtOp
import longevity.subdomain.query.LteOp
import longevity.subdomain.query.NeqOp
import longevity.subdomain.query.OrOp
import longevity.subdomain.query.Query
import longevity.subdomain.query.QueryFilter
import longevity.subdomain.query.QueryOrderBy
import longevity.subdomain.query.FilterAll
import longevity.subdomain.query.RelationalFilter
import longevity.subdomain.realized.RealizedPropComponent
import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.retrieveByQuery */
private[cassandra] trait CassandraQuery[P <: Persistent] {
  repo: CassandraRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext): Future[Seq[PState[P]]] =
    Future {
      logger.debug(s"calling CassandraRepo.retrieveByQuery: $query")
      val resultSet = blocking {
        queryResultSet(query)
      }
      val states = resultSet.all.toList.map(retrieveFromRow)
      logger.debug(s"done calling CassandraRepo.retrieveByQuery: $states")
      states
    }

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] = {
    logger.debug(s"calling CassandraRepo.streamByQuery: $query")
    def iterator(): Iterator[PState[P]] = {
      val resultSet = queryResultSet(query)
      import scala.collection.JavaConversions.asScalaIterator
      resultSet.iterator.map(retrieveFromRow)
    }
    // no need (or option) to clean up resources once stream terminates, because
    // Cassandra result set is paged, and does not support any close() operation
    val source = Source.fromIterator(iterator)
    logger.debug(s"done calling CassandraRepo.streamByQuery: $source")
    source
  }

  private def queryResultSet(query: Query[P]): ResultSet = {
    if (query.orderBy != QueryOrderBy.empty) throw new OrderByInQueryException
    if (query.offset.nonEmpty) throw new OffsetInQueryException

    val info = filterInfo(query.filter)
    val conjunction = queryWhereClause(info)
    val limit = query.limit.map(i => s" LIMIT $i").getOrElse("")
    val cql = s"SELECT * FROM $tableName WHERE $conjunction$limit ALLOW FILTERING"
    val bindings = info.bindValues
    logger.debug(s"executing CQL: $cql with bindings: $bindings")
    val boundStatement = preparedStatement(cql).bind(bindings: _*)
    session.execute(boundStatement)
  }

  protected def queryWhereClause(filterInfo: FilterInfo): String = filterInfo.whereClause

  protected case class FilterInfo(whereClause: String, bindValues: Seq[AnyRef])

  private def andFilterInfos(lhs: FilterInfo, rhs: FilterInfo) =
    FilterInfo(s"${lhs.whereClause} AND ${rhs.whereClause}", lhs.bindValues ++ rhs.bindValues)    

  private def filterInfo(filter: QueryFilter[P]): FilterInfo = filter match {
    case FilterAll() => throw new FilterAllInQueryException
    case RelationalFilter(lhs, op, rhs) => op match {
      case EqOp      => equalityQueryFilterInfo(lhs, rhs)
      case NeqOp     => throw new NeqInQueryException
      case LtOp      => orderingQueryFilterInfo(lhs, "<",  rhs)
      case LteOp     => orderingQueryFilterInfo(lhs, "<=", rhs)
      case GtOp      => orderingQueryFilterInfo(lhs, ">",  rhs)
      case GteOp     => orderingQueryFilterInfo(lhs, ">=", rhs)
    }
    case ConditionalFilter(lhs, op, rhs) => op match {
      case AndOp     => andFilterInfos(filterInfo(lhs), filterInfo(rhs))
      case OrOp      => throw new OrInQueryException
    }
  }

  private def equalityQueryFilterInfo[A](prop: Prop[_ >: P <: Persistent, A], value: A): FilterInfo = {
    val infos: Seq[FilterInfo] = toComponents(prop).map { component =>
      val componentValue = cassandraValue(component.innerPropPath.get(value))
      FilterInfo(s"${columnName(component)} = :${columnName(component)}", Seq(componentValue))
    }
    infos.tail.fold(infos.head)(andFilterInfos)
  }

  private def orderingQueryFilterInfo[A](prop: Prop[_ >: P <: Persistent, A], opString: String, value: A)
  : FilterInfo = {
    val components = toComponents(prop)
    def componentsToFilterInfo(components: Seq[RealizedPropComponent[_ >: P <: Persistent, A, _]]): FilterInfo = {
      if (components.size == 1) {
        def info[B](component: RealizedPropComponent[_ >: P <: Persistent, A, B]) = {
          val componentValue = cassandraValue(component.innerPropPath.get(value))
          FilterInfo(s"${columnName(component)} $opString :${columnName(component)}", Seq(componentValue))
        }
        info(components.head)
      } else {
        throw new CompoundPropInOrderingQuery
      }
    }
    componentsToFilterInfo(components)
  }

  def toComponents[A](prop: Prop[_ >: P <: Persistent, A])
  : Seq[RealizedPropComponent[_ >: P <: Persistent, A, _]] = {
    realizedPType.realizedProps(prop).realizedPropComponents
  }

}
