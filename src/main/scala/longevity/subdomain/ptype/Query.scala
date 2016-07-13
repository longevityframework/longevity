package longevity.subdomain.ptype

import Query._
import longevity.subdomain.persistent.Persistent

/** query operators and factory methods */
object Query {

  /** a query that filters nothing and returns everything */
  sealed case class All[P <: Persistent]() extends Query[P]

  /** those relational operators - namely, ''equals'' and ''not equals'' - that
   * apply, regardless of the types of the operands. this is in contrast to
   * relational operators such ''greater than'', which only apply to types that
   * have a ordering.
   */
  sealed trait EqualityOp

  /** the equals operator */
  case object EqOp extends EqualityOp

  /** the not equals operator */
  case object NeqOp extends EqualityOp

  /** relational operators that only apply to types that have an ordering */
  sealed trait OrderingOp

  /** the less than operator */
  case object LtOp extends OrderingOp

  /** the less than equals operator */
  case object LteOp extends OrderingOp

  /** the greater than operator */
  case object GtOp extends OrderingOp

  /** the greater than equals operator */
  case object GteOp extends OrderingOp

  /** either of the binary logical operators ''and'' and ''or'' */
  sealed trait LogicalOp

  /** the and operator */
  case object AndOp extends LogicalOp

  /** the or operator */
  case object OrOp extends LogicalOp

  /** a factory method for producing a [[EqualityQuery]] with an [[EqOp]] */
  def eqs[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    EqualityQuery[P, A](prop, EqOp, value)

  /** a factory method for producing a [[EqualityQuery]] with an [[NeqOp]] */
  def neq[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    EqualityQuery[P, A](prop, NeqOp, value)

  /** a factory method for producing a [[OrderingQuery]] with a [[LtOp]] */
  def lt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    OrderingQuery[P, A](prop, LtOp, value)

  /** a factory method for producing a [[OrderingQuery]] with a [[LteOp]] */
  def lte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    OrderingQuery[P, A](prop, LteOp, value)

  /** a factory method for producing a [[OrderingQuery]] with a [[GtOp]] */
  def gt[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    OrderingQuery[P, A](prop, GtOp, value)

  /** a factory method for producing a [[OrderingQuery]] with a [[LteOp]] */
  def gte[P <: Persistent, A](prop: Prop[_ >: P <: Persistent, A], value: A) =
    OrderingQuery[P, A](prop, GteOp, value)

  /** a factory method for producing a conditional [[Query]] with an [[AndOp]] */
  def and[P <: Persistent](lhs: Query[P], rhs: Query[P]) =
    ConditionalQuery[P](lhs, AndOp, rhs)

  /** a factory method for producing a conditional [[Query]] with an [[OrOp]] */
  def or[P <: Persistent](lhs: Query[P], rhs: Query[P]) =
    ConditionalQuery[P](lhs, OrOp, rhs)

}

/** a query for looking up persistent entities of type `P` */
sealed trait Query[P <: Persistent]

/** an equality query. compares a property to a value with an `eq` or an `neq`
 * operator.
 *
 * @param prop the property to compare
 * @param op the `eq` or `neq` operator
 * @param value the value to compare
 */
sealed case class EqualityQuery[P <: Persistent, A](
  val prop: Prop[_ >: P <: Persistent, A],
  op: EqualityOp,
  value: A)
extends Query[P]

/** an ordering query. compares a property to a value with a `lt`, `lte`, `gt`,
 * or `gte` operator.
 *
 * @param prop the property to compare
 * @param op the ordering operator
 * @param value the value to compare
 */
sealed case class OrderingQuery[P <: Persistent, A](
  val prop: Prop[_ >: P <: Persistent, A],
  op: OrderingOp,
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
