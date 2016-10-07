package longevity.subdomain.ptype

import QueryFilter._
import longevity.subdomain.Persistent

/** a DSL for creating [[Query queries]]. you can find it in your persistent
 * type at `PType.queryDsl`
 */
class QueryDsl[P <: Persistent] {

  /** begin parsing a query filter with a [[Prop]] */
  implicit def where[A](prop: Prop[_ >: P <: Persistent, A]) = new DslPostProp(prop)

  private[QueryDsl] case class CondPrefix(lhs: QueryFilter[P], op: LogicalOp) {
    def buildCond(rhs: QueryFilter[P]) = ConditionalFilter[P](lhs, op, rhs)
  }

  /** in the query DSL, we have just parsed a property. next we need to parse a
   * [[RelationalOp relational operator]] and a right-hand side value.
   */
  class DslPostProp[A] private[QueryDsl] (
    private val prop: Prop[_ >: P <: Persistent, A],
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
   * [[QueryFilter]]. there are multiple possibilities for what comes next:
   *
   * 1. if we see a [[ConditionalOp]] followed by a
   * property, then we need to parse another [[RelationalFilter]].
   *
   * 2. if we see a [[ConditionalOp]] followed by another
   * [[QueryFilter]], then we combine the two query filters with the
   * conditional op.
   * 
   * 3. we could be done parsing the complete [[QueryFilter]].
   */
  class DslPostQueryFilter private[QueryDsl] (private[QueryDsl] val prefix: QueryFilter[P]) {

    /** parse an `and` token and the next property, and prepare for a relational operator */
    def and[A](prop: Prop[_ >: P <: Persistent, A]) =
      new DslPostProp(prop, Some(CondPrefix(prefix, AndOp)))

    /** parse an `and` token a (possibly parenthesized) query, and prepare for a logical operator */
    def and(filter: QueryFilter[P]) =
      new DslPostQueryFilter(ConditionalFilter(prefix, AndOp, filter))

    /** parse an `or` token and the next property, and prepare for a relational operator */
    def or[A](prop: Prop[_ >: P <: Persistent, A]) =
      new DslPostProp(prop, Some(CondPrefix(prefix, OrOp)))

    /** parse an `or` token a (possibly parenthesized) query, and prepare for a logical operator */
    def or(filter: QueryFilter[P]) =
      new DslPostQueryFilter(ConditionalFilter(prefix, OrOp, filter))

  }

  /** we are done parsing a complete [[QueryFilter]] */
  implicit def toQueryFilter(postFilter: DslPostQueryFilter): QueryFilter[P] = postFilter.prefix

  /** we are done parsing a complete [[Query]] */
  implicit def toQuery(postFilter: DslPostQueryFilter): Query[P] = Query(postFilter.prefix)

}
