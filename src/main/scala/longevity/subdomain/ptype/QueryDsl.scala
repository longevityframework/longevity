package longevity.subdomain.ptype

import Query._
import longevity.subdomain.Persistent

/** a DSL for creating [[Query queries]]. you can find it in your persistent
 * type at `PType.queryDsl`
 */
class QueryDsl[P <: Persistent] {

  /** start building a query with a [[Prop]] */
  implicit def where[A](prop: Prop[_ >: P <: Persistent, A]) = new GatherRelational(prop)

  private[QueryDsl] case class CondPrefix(lhs: Query[P], op: LogicalOp) {
    def buildCond(rhs: Query[P]) = ConditionalQuery[P](lhs, op, rhs)
  }

  /** gathering the rest of a relational expression on a property, such as
   *
   * ```
   * object User extends PType[User] {
   *   object props {
   *     val accountNumber = prop[String]("account.number")
   *   }
   *   object keys {
   *   }
   *   object indexes {
   *   }
   * }
   * User.props.accountNumber eqs "D85330"`
   * ```
   */
  class GatherRelational[A] private[QueryDsl] (
    private val prop: Prop[_ >: P <: Persistent, A],
    private val prefix: Option[CondPrefix] = None) {

    /** gather an `eqs` expression, and prepare for an `and` or an `or` */
    def eqs(a: A) = {
      val rhs = EqualityQuery[P, A](prop, EqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `neq` expression, and prepare for an `and` or an `or` */
    def neq(a: A) = {
      val rhs = EqualityQuery[P, A](prop, NeqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lt` expression, and prepare for an `and` or an `or` */
    def lt(a: A) = {
      val rhs = OrderingQuery[P, A](prop, LtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather an `lte` expression, and prepare for an `and` or an `or` */
    def lte(a: A) = {
      val rhs = OrderingQuery[P, A](prop, LteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gt` expression, and prepare for an `and` or an `or` */
    def gt(a: A) = {
      val rhs = OrderingQuery[P, A](prop, GtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    /** gather a `gte` expression, and prepare for an `and` or an `or` */
    def gte(a: A) = {
      val rhs = OrderingQuery[P, A](prop, GteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  /** gathering the rest of a logical expression, combining two expressions with
   * and `and` or an `or`
   */
  class GatherLogical private[QueryDsl] (private[QueryDsl] val prefix: Query[P]) {

    /** gather an `and` token and the next property, and prepare for a relational operator */
    def and[A](prop: Prop[_ >: P <: Persistent, A]) =
      new GatherRelational(prop, Some(CondPrefix(prefix, AndOp)))

    /** gather an `and` token a (possibly parenthesized) query, and prepare for a logical operator */
    def and(query: Query[P]) =
      new GatherLogical(ConditionalQuery(prefix, AndOp, query))

    /** gather an `or` token and the next property, and prepare for a relational operator */
    def or[A](prop: Prop[_ >: P <: Persistent, A]) =
      new GatherRelational(prop, Some(CondPrefix(prefix, OrOp)))

    /** gather an `or` token a (possibly parenthesized) query, and prepare for a logical operator */
    def or(query: Query[P]) =
      new GatherLogical(ConditionalQuery(prefix, OrOp, query))

  }

  /** terminate gathering the expression and produce it */
  implicit def queryProduce(gather: GatherLogical): Query[P] = gather.prefix

}
