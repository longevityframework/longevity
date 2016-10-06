package longevity.subdomain.ptype

import Query._
import longevity.subdomain.Persistent

/** query operators and factory methods */
object Query {

  /** a query that filters nothing and returns everything */
  sealed case class All[P <: Persistent]() extends Query[P]

  /** a query relational operator. compares a persistent property to a raw value */
  sealed trait RelationalOp

  /** the equals operator */
  case object EqOp extends RelationalOp

  /** the not equals operator */
  case object NeqOp extends RelationalOp

  /** the less than operator */
  case object LtOp extends RelationalOp

  /** the less than equals operator */
  case object LteOp extends RelationalOp

  /** the greater than operator */
  case object GtOp extends RelationalOp

  /** the greater than equals operator */
  case object GteOp extends RelationalOp

  /** either of the binary logical operators ''and'' and ''or'' */
  sealed trait LogicalOp

  /** the and operator */
  case object AndOp extends LogicalOp

  /** the or operator */
  case object OrOp extends LogicalOp

  /** a factory method for producing a [[RelationalQuery]] with an [[EqOp]] */
  def eqs[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, EqOp, value)

  /** a factory method for producing a [[RelationalQuery]] with an [[NeqOp]] */
  def neq[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, NeqOp, value)

  /** a factory method for producing a [[RelationalQuery]] with a [[LtOp]] */
  def lt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, LtOp, value)

  /** a factory method for producing a [[RelationalQuery]] with a [[LteOp]] */
  def lte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, LteOp, value)

  /** a factory method for producing a [[RelationalQuery]] with a [[GtOp]] */
  def gt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, GtOp, value)

  /** a factory method for producing a [[RelationalQuery]] with a [[LteOp]] */
  def gte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    RelationalQuery[P, A](prop, GteOp, value)

  /** a factory method for producing a conditional [[Query]] with an [[AndOp]] */
  def and[P <: Persistent](lhs: Query[P], rhs: Query[P]) =
    ConditionalQuery[P](lhs, AndOp, rhs)

  /** a factory method for producing a conditional [[Query]] with an [[OrOp]] */
  def or[P <: Persistent](lhs: Query[P], rhs: Query[P]) =
    ConditionalQuery[P](lhs, OrOp, rhs)

}

/** a query for looking up persistent entities of type `P` */
sealed trait Query[P <: Persistent]

/** an equality query. compares a property to a value with an `eq`, `neq`, `lt`,
 * `lte`, `gt`, or `gte` operator.
 *
 * @param prop the property to compare
 * @param op the relational operator
 * @param value the value to compare
 */
sealed case class RelationalQuery[P <: Persistent, A](
  val prop: Prop[_ >: P <: Persistent, A],
  op: RelationalOp,
  value: A)
extends Query[P]

/** a conditional query. combines two sub-queries with an `and` or an `or`
 * operator.
 *
 * @param lhs the left-hand side sub-query
 * @param op the `and` or `or` operator
 * @param rhs the right-hand side sub-query
 */
sealed case class ConditionalQuery[P <: Persistent](
  lhs: Query[P],
  op: LogicalOp,
  rhs: Query[P])
extends Query[P]
