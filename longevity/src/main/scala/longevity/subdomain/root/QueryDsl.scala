package longevity.subdomain.root

import Query._
import emblem.imports._
import longevity.subdomain._


/** a DSL for creating [[Query queries]]. you can find it in your local repository at `Repo.queryDsl` */
class QueryDsl[R <: RootEntity] {

  /** start building a query with a path */
  implicit def where(path: String) = new GatherDRelational(path)

  /** start building a query with a [[Prop]] */
  implicit def where[A](prop: Prop[R, A]) = new GatherSRelational(prop)

  private[QueryDsl] case class CondPrefix(lhs: Query[R], op: LogicalOp) {
    def buildCond(rhs: Query[R]) = Query.cond(lhs, op, rhs)
  }

  /** gathering the rest of a relational expression on a property path, such as
   * `"account.number" eqs "D85330"`
   */
  class GatherDRelational private[QueryDsl] (
    private val path: String,
    private val prefix: Option[CondPrefix] = None) {

    /** gather an `eqs` expression, and prepare for an `and` or an `or` */
    def eqs[A : TypeKey](a: A) = {
      val rhs = EqualityQuery[R, A](path, EqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `neq` expression, and prepare for an `and` or an `or` */
    def neq[A : TypeKey](a: A) = {
      val rhs = EqualityQuery[R, A](path, NeqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lt` expression, and prepare for an `and` or an `or` */
    def lt[A : TypeKey](a: A) = {
      val rhs = OrderingQuery[R, A](path, LtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lte` expression, and prepare for an `and` or an `or` */
    def lte[A : TypeKey](a: A) = {
      val rhs = OrderingQuery[R, A](path, LteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gt` expression, and prepare for an `and` or an `or` */
    def gt[A : TypeKey](a: A) = {
      val rhs = OrderingQuery[R, A](path, GtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gte` expression, and prepare for an `and` or an `or` */
    def gte[A : TypeKey](a: A) = {
      val rhs = OrderingQuery[R, A](path, GteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  /** gathering the rest of a relational expression on a property, such as
   *
   * ```
   * object User extends RootType[User] {
   *   val accountNoProp = prop[String]("account.number")
   * }
   * User.accountNoProp eqs "D85330"`
   * ```
   */
  class GatherSRelational[A] private[QueryDsl] (
    private val prop: Prop[R, A],
    private val prefix: Option[CondPrefix] = None) {

    /** gather an `eqs` expression, and prepare for an `and` or an `or` */
    def eqs(a: A) = {
      val rhs = VEqualityQuery[R, A](prop, EqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `neq` expression, and prepare for an `and` or an `or` */
    def neq(a: A) = {
      val rhs = VEqualityQuery[R, A](prop, NeqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lt` expression, and prepare for an `and` or an `or` */
    def lt(a: A) = {
      val rhs = VOrderingQuery[R, A](prop, LtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lte` expression, and prepare for an `and` or an `or` */
    def lte(a: A) = {
      val rhs = VOrderingQuery[R, A](prop, LteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gt` expression, and prepare for an `and` or an `or` */
    def gt(a: A) = {
      val rhs = VOrderingQuery[R, A](prop, GtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gte` expression, and prepare for an `and` or an `or` */
    def gte(a: A) = {
      val rhs = VOrderingQuery[R, A](prop, GteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  /** gathering the rest of a logical expression, combining two expressions with and `and` or an `or` */
  class GatherLogical private[QueryDsl] (private[QueryDsl] val prefix: Query[R]) {

    /** gather an `and` token and the next path, and prepare for a relational operator */
    def and(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, AndOp)))

    /** gather an `and` token and the next property, and prepare for a relational operator */
    def and[A](prop: Prop[R, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, AndOp)))

    /** gather an `and` token a (possibly parenthesized) query, and prepare for a logical operator */
    def and(query: Query[R]) = new GatherLogical(Query.cond(prefix, AndOp, query))

    /** gather an `or` token and the next path, and prepare for a relational operator */
    def or(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, OrOp)))

    /** gather an `or` token and the next property, and prepare for a relational operator */
    def or[A](prop: Prop[R, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, OrOp)))

    /** gather an `or` token a (possibly parenthesized) query, and prepare for a logical operator */
    def or(query: Query[R]) = new GatherLogical(Query.cond(prefix, OrOp, query))

  }

  /** terminate gathering the expression and produce it */
  implicit def queryProduce(gather: GatherLogical): Query[R] = gather.prefix

}
