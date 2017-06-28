package longevity.persistence.jdbc

import java.sql.ResultSet
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
import longevity.model.query.RelationalOp
import longevity.model.realized.RealizedPropComponent
import longevity.persistence.PState
import scala.collection.immutable.VectorBuilder
import streamadapter.CloseableChunkIter
import streamadapter.Chunkerator

/** implementation of JdbcPRepo.retrieveByQuery */
private[jdbc] trait JdbcQuery[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  protected def queryToChunkerator(query: Query[P]) = {
    logger.debug(s"calling JdbcPRepo.queryToChunkerator: $query")
    val c = new Chunkerator[PState[P]] {
      def apply = new CloseableChunkIter[PState[P]] {
        private val resultSet = queryResultSet(query)
        private var nextResult = resultSet.next
        def hasNext = nextResult
        def next = {
          var i = 0
          val builder = new VectorBuilder[PState[P]]()
          do {
            builder += retrieveFromResultSet(resultSet)
            i += 1
            nextResult = resultSet.next
          } while (i < 20 && hasNext)
          builder.result
        }
        def close = resultSet.close
      }
    }
    logger.debug(s"done calling JdbcPRepo.queryToChunkerator")
    c
  }

  private def queryResultSet(query: Query[P]): ResultSet = {
    val info = filterInfo(query.filter)
    val conjunction = queryWhereClause(info)
    val orderBy = queryOrderByClause(query.orderBy)
    val limit = query.limit.map(i => s"\nLIMIT $i").getOrElse("")
    val offset = query.offset.map(i => s"\OFFSET $i").getOrElse("")

    val sql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $conjunction$orderBy$limit$offset
    |""".stripMargin
    val bindings = info.bindValues
    logger.debug(s"executing SQL: $sql with bindings: $bindings")
    val statement = connection().prepareStatement(sql)
    bindings.zipWithIndex.foreach { case (binding, index) =>
      statement.setObject(index + 1, binding)
    }
    statement.executeQuery()
  }

  private def queryOrderByClause(orderBy: QueryOrderBy[P]): String = {
    if (orderBy == QueryOrderBy.empty) {
      ""
    } else {
      val orderings = orderBy.sortExprs.flatMap { sortExpr =>
        val direction = sortExpr.direction match {
          case Ascending => "ASC"
          case Descending => "DESC"
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
    FilterInfo(s"(${lhs.whereClause} AND ${rhs.whereClause})", lhs.bindValues ++ rhs.bindValues)    

  private def orFilterInfos(lhs: FilterInfo, rhs: FilterInfo) =
    FilterInfo(s"(${lhs.whereClause} OR ${rhs.whereClause})", lhs.bindValues ++ rhs.bindValues)    

  private def filterInfo(filter: QueryFilter[P]): FilterInfo = filter match {
    case FilterAll() => FilterInfo("1 = 1", Seq())
    case RelationalFilter(lhs, op, rhs) => op match {
      case EqOp      => equalityQueryFilterInfo(lhs, rhs, false)
      case NeqOp     => equalityQueryFilterInfo(lhs, rhs, true)
      case LtOp      => orderingQueryFilterInfo(lhs, LtOp,  rhs)
      case LteOp     => orderingQueryFilterInfo(lhs, LteOp, rhs)
      case GtOp      => orderingQueryFilterInfo(lhs, GtOp,  rhs)
      case GteOp     => orderingQueryFilterInfo(lhs, GteOp, rhs)
    }
    case ConditionalFilter(lhs, op, rhs) => op match {
      case AndOp     => andFilterInfos(filterInfo(lhs), filterInfo(rhs))
      case OrOp      => orFilterInfos(filterInfo(lhs), filterInfo(rhs))
    }
  }

  private def equalityQueryFilterInfo[A](prop: Prop[_ >: P, A], value: A, neq: Boolean): FilterInfo = {
    val infos: Seq[FilterInfo] = toComponents(prop).map { component =>
      val componentValue = jdbcValue(component.innerPropPath.get(value))
      val op = if (neq) "!=" else "="
      FilterInfo(s"${columnName(component)} $op ?", Seq(componentValue))
    }
    infos.tail.fold(infos.head)(andFilterInfos)
  }

  private def orderingQueryFilterInfo[A](prop: Prop[_ >: P, A], op: RelationalOp, value: A): FilterInfo = {
    type Component = RealizedPropComponent[_ >: P, A, _]
    val components = toComponents(prop)

    def cname(c: Component) = columnName(c)
    def cval(c: Component) = Seq(jdbcValue(c.innerPropPath.get(value)))

    def eqC(c: Component) = FilterInfo(s"${cname(c)} = ?",  cval(c))
    def neC(c: Component) = FilterInfo(s"${cname(c)} != ?", cval(c))
    def ltC(c: Component) = FilterInfo(s"${cname(c)} < ?",  cval(c))
    def gtC(c: Component) = FilterInfo(s"${cname(c)} > ?",  cval(c))

    def wheres(fs: Seq[FilterInfo], conj: String) = s"(${fs.map(_.whereClause).mkString(conj)})"
    def binds(fs: Seq[FilterInfo]) = fs.map(_.bindValues).flatten
    def and(fs: Seq[FilterInfo]) = FilterInfo(wheres(fs, " AND "), binds(fs))
    def or (fs: Seq[FilterInfo]) = FilterInfo(wheres(fs, " OR "),  binds(fs))

    def eq = and { components.map(eqC) }
    def ne = or  { components.map(neC) }

    def diff(f: (Component) => FilterInfo) = or {      
      for { i <- 0 until components.length } yield {
        and {
          components.take(i).map(eqC) :+ f(components(i))
        }
      }
    }
 
    def lt = diff(ltC)
    def gt = diff(gtC)

    op match {
      case EqOp  => eq
      case NeqOp => ne
      case LtOp  => lt
      case LteOp => or(Seq(lt, eq))
      case GtOp  => gt
      case GteOp => or(Seq(gt, eq))
    }
  }

  private def toComponents[A](prop: Prop[_ >: P, A]): Seq[RealizedPropComponent[_ >: P, A, _]] = {
    realizedPType.realizedProps(prop).realizedPropComponents
  }

}
