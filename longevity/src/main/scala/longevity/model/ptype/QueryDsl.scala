package longevity.model.ptype

import longevity.model.query.AndOp
import longevity.model.query.Ascending
import longevity.model.query.ConditionalFilter
import longevity.model.query.Descending
import longevity.model.query.EqOp
import longevity.model.query.FilterAll
import longevity.model.query.GtOp
import longevity.model.query.GteOp
import longevity.model.query.LogicalOp
import longevity.model.query.LtOp
import longevity.model.query.LteOp
import longevity.model.query.NeqOp
import longevity.model.query.OrOp
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.model.query.QuerySortExpr
import longevity.model.query.RelationalFilter

/** a DSL for creating [[longevity.subdomain.query.Query queries]]. you can find
 * it in your persistent type at `PType.queryDsl`
 */
class QueryDsl[P] {

  /** begin parsing a query filter with a [[Prop]] */
  implicit def where[A](prop: Prop[_ >: P, A]) = new DslPostProp(prop)

  /** begin parsing with a `FilterAll` query filter */
  def filterAll = new DslPostQueryFilter(FilterAll())

  private[QueryDsl] case class CondPrefix(lhs: QueryFilter[P], op: LogicalOp) {
    def buildCond(rhs: QueryFilter[P]) = ConditionalFilter[P](lhs, op, rhs)
  }

  /** in the query DSL, we have just parsed a property. next we need to parse a
   * [[longevity.subdomain.query.RelationalOp relational operator]] and a
   * right-hand side value.
   */
  class DslPostProp[A] private[QueryDsl] (
    private val prop: Prop[_ >: P, A],
    private val prefix: Option[CondPrefix] = None) {

    /** parse an `eqs` expression, and prepare for an `and` or an `or` */
    def eqs(a: A) = {
      val rhs = RelationalFilter[P, A](prop, EqOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

    /** parse an `neq` expression, and prepare for an `and` or an `or` */
    def neq(a: A) = {
      val rhs = RelationalFilter[P, A](prop, NeqOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

    /** parse a `lt` expression, and prepare for an `and` or an `or` */
    def lt(a: A) = {
      val rhs = RelationalFilter[P, A](prop, LtOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

    /** parse a `lte` expression, and prepare for an `and` or an `or` */
    def lte(a: A) = {
      val rhs = RelationalFilter[P, A](prop, LteOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

    /** parse a `gt` expression, and prepare for an `and` or an `or` */
    def gt(a: A) = {
      val rhs = RelationalFilter[P, A](prop, GtOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

    /** parse a `gte` expression, and prepare for an `and` or an `or` */
    def gte(a: A) = {
      val rhs = RelationalFilter[P, A](prop, GteOp, a)
      val filter = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new DslPostQueryFilter(filter)
    }

  }

  /** in the query DSL, we have just parsed a (partial or complete)
   * [[longevity.subdomain.query.QueryFilter QueryFilter]]. there are multiple
   * possibilities for what comes next:
   *
   * 1. if we see a [[longevity.subdomain.query.LogicalOp LogicalOp]]
   * followed by another [[longevity.subdomain.query.QueryFilter QueryFilter]],
   * then we combine the two query filters with the logical op.
   * 
   * 2. we parse an order-by clause
   * 
   * 3. we parse an offset clause
   * 
   * 4. we parse a limit clause
   * 
   * 5. we are done parsing the complete
   * [[longevity.subdomain.query.QueryFilter QueryFilter]].
   */
  class DslPostQueryFilter private[QueryDsl] (private[QueryDsl] val prefix: QueryFilter[P]) {

    /** parse an `and` token and the next property, and prepare for a relational operator */
    def and[A](prop: Prop[_ >: P, A]) =
      new DslPostProp(prop, Some(CondPrefix(prefix, AndOp)))

    /** parse an `and` token a (possibly parenthesized) query, and prepare for a logical operator */
    def and(filter: QueryFilter[P]) =
      new DslPostQueryFilter(ConditionalFilter(prefix, AndOp, filter))

    /** parse an `or` token and the next property, and prepare for a relational operator */
    def or[A](prop: Prop[_ >: P, A]) =
      new DslPostProp(prop, Some(CondPrefix(prefix, OrOp)))

    /** parse an `or` token a (possibly parenthesized) query, and prepare for a logical operator */
    def or(filter: QueryFilter[P]) =
      new DslPostQueryFilter(ConditionalFilter(prefix, OrOp, filter))

    /** parse an `orderBy` clause, and prepare for optional offset and limit clauses */
    def orderBy[A](ses: QuerySortExpr[P]*) = new DslPostOrderBy(
      prefix,
      QueryOrderBy(ses))

    /** parse an `offset` clause, and prepare for an optional limit clause */
    def offset(i: Int) = new DslPostOffset(prefix, QueryOrderBy.empty, Some(i))

    /** parse a `limit` clause, and prepare for whatever comes after the offset (spoiler: nothing) */
    def limit(i: Int) = new DslPostLimit(prefix, QueryOrderBy.empty, None, Some(i))

  }

  /** we are done parsing a complete [[longevity.subdomain.query.QueryFilter QueryFilter]] */
  implicit def toQueryFilter(postFilter: DslPostQueryFilter): QueryFilter[P] = postFilter.prefix
 
  /** we are done parsing a complete [[longevity.subdomain.query.Query Query]] */
  implicit def toQuery(postFilter: DslPostQueryFilter): Query[P] = Query(postFilter.prefix)

  /** we parse a `Prop` into a `QuerySortExpr` as needed */
  implicit def toQuerySortExpr[A](prop: Prop[_ >: P, A]) = new QuerySortExpr[P](prop, Ascending)

  /** we have parsed a `Prop` for a `QuerySortExpr`, now we are ready to parse an `asc` or `desc` qualifier */
  class UnqualifiedSortExpr(val prop: Prop[_ >: P, _]) {
    def asc = new QuerySortExpr[P](prop, Ascending)
    def desc = new QuerySortExpr[P](prop, Descending)
  }

  /** parse a `Prop` and prepare to parse an `asc` or `desc` qualified */
  implicit def toUnqualifiedSortExpr(prop: Prop[_ >: P, _]) = new UnqualifiedSortExpr(prop)

  /** in the query DSL, we have just parsed a
   * [[longevity.subdomain.query.QueryFilter QueryFilter]] and a
   * [[longevity.subdomain.query.QueryOrderBy QueryOrderBy]]. there are multiple
   * possibilities for what comes next:
   *
   * 1. we parse an offset clause
   * 
   * 2. we parse a limit clause
   * 
   * 3. we are done parsing the complete
   * [[longevity.subdomain.query.QueryFilter QueryFilter]].
   */
  class DslPostOrderBy private[QueryDsl] (
    private[QueryDsl] val prefix: QueryFilter[P],
    private[QueryDsl] val orderBy: QueryOrderBy[P]) {

    /** parse an `offset` clause, and prepare for an optional limit clause */
    def offset(i: Int) = new DslPostOffset(prefix, orderBy, Some(i))

    /** parse a `limit` clause, and prepare for whatever comes after the offset (spoiler: nothing) */
    def limit(i: Int) = new DslPostLimit(prefix, orderBy, None, Some(i))

  }

  /** we are done parsing a complete [[longevity.subdomain.query.Query Query]] */
  implicit def toQuery(postOrderBy: DslPostOrderBy): Query[P] =
    Query(postOrderBy.prefix, postOrderBy.orderBy)

  /** in the query DSL, we have parsed a
   * [[longevity.subdomain.query.QueryFilter QueryFilter]], a
   * [[longevity.subdomain.query.QueryOrderBy QueryOrderBy]], and an offset
   * clause. there are two possibilities for what comes next:
   *
   * 1. we parse a limit clause
   * 
   * 2. we are done parsing the complete
   * [[longevity.subdomain.query.QueryFilter QueryFilter]].
   */
  class DslPostOffset private[QueryDsl] (
    private[QueryDsl] val prefix: QueryFilter[P],
    private[QueryDsl] val orderBy: QueryOrderBy[P],
    private[QueryDsl] val offset: Option[Int]) {

    /** parse a `limit` clause, and prepare for whatever comes after the offset (spoiler: nothing) */
    def limit(i: Int) = new DslPostLimit(prefix, orderBy, offset, Some(i))

  }

  /** we are done parsing a complete [[longevity.subdomain.query.Query Query]] */
  implicit def toQuery(postOffset: DslPostOffset): Query[P] =
    Query(postOffset.prefix, postOffset.orderBy, postOffset.offset)

  /** in the query DSL, we have parsed a
   * [[longevity.subdomain.query.QueryFilter QueryFilter]], a
   * [[longevity.subdomain.query.QueryOrderBy QueryOrderBy]], an offset
   * and a limit clause. there is one possibility for what comes next:
   * 
   * 1. we are done parsing the complete
   * [[longevity.subdomain.query.QueryFilter QueryFilter]].
   */
  class DslPostLimit private[QueryDsl] (
    private[QueryDsl] val prefix: QueryFilter[P],
    private[QueryDsl] val orderBy: QueryOrderBy[P],
    private[QueryDsl] val offset: Option[Int],
    private[QueryDsl] val limit: Option[Int])

  /** we are done parsing a complete [[longevity.subdomain.query.Query Query]] */
  implicit def toQuery(postLimit: DslPostLimit): Query[P] =
    Query(postLimit.prefix, postLimit.orderBy, postLimit.offset, postLimit.limit)

}
