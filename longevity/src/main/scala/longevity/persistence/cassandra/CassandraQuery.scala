package longevity.persistence.cassandra

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.datastax.driver.core.ResultSet
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.exceptions.persistence.cassandra.FilterAllInQueryException
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OffsetInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.persistence.PState
import longevity.model.ptype.Prop
import longevity.model.query.AndOp
import longevity.model.query.Ascending
import longevity.model.query.ConditionalFilter
import longevity.model.query.Descending
import longevity.model.query.EqOp
import longevity.model.query.FilterAll
import longevity.model.query.GtOp
import longevity.model.query.GteOp
import longevity.model.query.LtOp
import longevity.model.query.LteOp
import longevity.model.query.NeqOp
import longevity.model.query.OrOp
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.model.query.RelationalFilter
import longevity.model.realized.RealizedPropComponent
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.retrieveByQuery */
private[cassandra] trait CassandraQuery[P] {
  repo: CassandraRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext): Future[Seq[PState[P]]] =
    Future {
      logger.debug(s"calling CassandraRepo.retrieveByQuery: $query")
      val resultSet = blocking {
        queryResultSet(query)
      }
      val states = resultSet.all.asScala.map(retrieveFromRow)
      logger.debug(s"done calling CassandraRepo.retrieveByQuery: $states")
      states
    }

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] = {
    logger.debug(s"calling CassandraRepo.streamByQuery: $query")
    val iterator: () => Iterator[PState[P]] = { () =>
      val resultSet = queryResultSet(query)
      resultSet.iterator.asScala.map(retrieveFromRow)
    }
    // no need (or option) to clean up resources once stream terminates, because
    // Cassandra result set is paged, and does not support any close() operation
    val source = Source.fromIterator(iterator)
    logger.debug(s"done calling CassandraRepo.streamByQuery: $source")
    source
  }

  private def queryResultSet(query: Query[P]): ResultSet = {
    if (query.offset.nonEmpty) throw new OffsetInQueryException

    val info = filterInfo(query.filter)
    val conjunction = queryWhereClause(info)
    val orderBy = queryOrderByClause(query.orderBy)
    val limit = query.limit.map(i => s"\nLIMIT $i").getOrElse("")
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction$orderBy$limit
    |ALLOW FILTERING
    |""".stripMargin
    val bindings = info.bindValues
    logger.debug(s"executing CQL: $cql with bindings: $bindings")
    val boundStatement = preparedStatement(cql).bind(bindings: _*)
    session.execute(boundStatement)
  }

  private def queryOrderByClause(orderBy: QueryOrderBy[P]): String = {
    if (orderBy == QueryOrderBy.empty) {
      ""
    } else {
      val orderings = orderBy.sortExprs.flatMap { sortExpr =>
        val direction = sortExpr.direction match {
          case Ascending => "asc"
          case Descending => "desc"
        }
        toComponents(sortExpr.prop).map { component =>
          s"${columnName(component)} $direction"
        }
      }
      val orderingsString = orderings.mkString(", ")
      s"\nORDER BY\n  $orderingsString"
    }
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

  private def equalityQueryFilterInfo[A](prop: Prop[_ >: P, A], value: A): FilterInfo = {
    val infos: Seq[FilterInfo] = toComponents(prop).map { component =>
      val componentValue = cassandraValue(component.innerPropPath.get(value))
      FilterInfo(s"${columnName(component)} = :${columnName(component)}", Seq(componentValue))
    }
    infos.tail.fold(infos.head)(andFilterInfos)
  }

  private def orderingQueryFilterInfo[A](prop: Prop[_ >: P, A], opString: String, value: A)
  : FilterInfo = {
    val components = toComponents(prop)
    def componentsToFilterInfo(components: Seq[RealizedPropComponent[_ >: P, A, _]]): FilterInfo = {
      if (components.size == 1) {
        def info[B](component: RealizedPropComponent[_ >: P, A, B]) = {
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

  private def toComponents[A](prop: Prop[_ >: P, A]): Seq[RealizedPropComponent[_ >: P, A, _]] = {
    realizedPType.realizedProps(prop).realizedPropComponents
  }

}
