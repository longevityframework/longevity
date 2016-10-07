package longevity.subdomain.query

import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Prop

/** a query filter for looking up persistent entities of type `P` */
sealed trait QueryFilter[P <: Persistent]

/** a query that filters nothing and returns everything */
sealed case class FilterAll[P <: Persistent]() extends QueryFilter[P]

/** an equality query filter. compares a property to a value with an `eq`,
 * `neq`, `lt`, `lte`, `gt`, or `gte` operator.
 *
 * @param prop the property to compare
 * @param op the relational operator
 * @param value the value to compare
 */
sealed case class RelationalFilter[P <: Persistent, A](
  val prop: Prop[_ >: P <: Persistent, A],
  op: RelationalOp,
  value: A)
extends QueryFilter[P]

/** a conditional query filter. combines two sub-queries with an `and` or an
 * `or` operator.
 *
 * @param lhs the left-hand side sub-query
 * @param op the `and` or `or` operator
 * @param rhs the right-hand side sub-query
 */
sealed case class ConditionalFilter[P <: Persistent](
  lhs: QueryFilter[P],
  op: LogicalOp,
  rhs: QueryFilter[P])
extends QueryFilter[P]

/** query filter factory methods */
object QueryFilter {

  /** a factory method for producing a [[RelationalFilter]] with an [[EqOp]] */
  def eqs[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, EqOp, value)

  /** a factory method for producing a [[RelationalFilter]] with an [[NeqOp]] */
  def neq[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, NeqOp, value)

  /** a factory method for producing a [[RelationalFilter]] with a [[LtOp]] */
  def lt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, LtOp, value)

  /** a factory method for producing a [[RelationalFilter]] with a [[LteOp]] */
  def lte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, LteOp, value)

  /** a factory method for producing a [[RelationalFilter]] with a [[GtOp]] */
  def gt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, GtOp, value)

  /** a factory method for producing a [[RelationalFilter]] with a [[LteOp]] */
  def gte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalFilter[P, A](prop, GteOp, value)

  /** a factory method for producing a conditional [[QueryFilter]] with an [[AndOp]] */
  def and[P <: Persistent](lhs: QueryFilter[P], rhs: QueryFilter[P]) =
    ConditionalFilter[P](lhs, AndOp, rhs)

  /** a factory method for producing a conditional [[QueryFilter]] with an [[OrOp]] */
  def or[P <: Persistent](lhs: QueryFilter[P], rhs: QueryFilter[P]) =
    ConditionalFilter[P](lhs, OrOp, rhs)

}
