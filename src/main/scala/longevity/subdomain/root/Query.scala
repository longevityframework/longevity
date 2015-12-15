package longevity.subdomain.root

import Query._
import emblem.imports._
import longevity.subdomain._

/** query operators and factory methods */
object Query {

  /** those relational operators - namely, _equals_ and _not equals_ - that apply, regardless of the types
   * of the operands. this is in contrast to relational operators such _greater than_, which only apply to
   * types that have a ordering
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

  /** either of the binary logical operators _and_ and _or_ */
  sealed trait LogicalOp

  /** the and operator */
  case object AndOp extends LogicalOp

  /** the or operator */
  case object OrOp extends LogicalOp

  /** a factory method for producing an [[EqualityQuery]] with an [[EqOp]] */
  def eqs[R <: Root, A : TypeKey](path: String, value: A) =
    EqualityQuery[R, A](path, EqOp, value)

  /** a factory method for producing a [[VEqualityQuery]] with an [[EqOp]] */
  def eqs[R <: Root, A](prop: Prop[R, A], value: A) =
    VEqualityQuery[R, A](prop, EqOp, value)

  /** a factory method for producing an [[EqualityQuery]] with an [[NeqOp]] */
  def neq[R <: Root, A : TypeKey](path: String, value: A) =
    EqualityQuery[R, A](path, NeqOp, value)

  /** a factory method for producing a [[VEqualityQuery]] with an [[NeqOp]] */
  def neq[R <: Root, A](prop: Prop[R, A], value: A) =
    VEqualityQuery[R, A](prop, NeqOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[LtOp]] */
  def lt[R <: Root, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, LtOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LtOp]] */
  def lt[R <: Root, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, LtOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[LteOp]] */
  def lte[R <: Root, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, LteOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LteOp]] */
  def lte[R <: Root, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, LteOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[GtOp]] */
  def gt[R <: Root, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, GtOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[GtOp]] */
  def gt[R <: Root, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, GtOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[GteOp]] */
  def gte[R <: Root, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, GteOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LteOp]] */
  def gte[R <: Root, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, GteOp, value)

  /** a factory method for producing a conditional [[Query]] with an [[AndOp]] */
  def and[R <: Root](lhs: Query[R], rhs: Query[R]) = cond(lhs, AndOp, rhs)

  /** a factory method for producing a conditional [[Query]] with an [[OrOp]] */
  def or[R <: Root](lhs: Query[R], rhs: Query[R]) = cond(lhs, OrOp, rhs)

  private[root] def cond[R <: Root](lhs: Query[R], op: LogicalOp, rhs: Query[R]) = (lhs, rhs) match {
    case (lhs: ValidatedQuery[R], rhs: ValidatedQuery[R]) => VConditionalQuery[R](lhs, op, rhs)
    case _ => ConditionalQuery[R](lhs, op, rhs)
  }

}

/** a query for looking up aggregates of type `R` */
sealed trait Query[R <: Root]

/** a query where all the types check out */
sealed trait ValidatedQuery[R <: Root] extends Query[R]

/** an equality query where the left-hand side is a property path */
sealed case class EqualityQuery[R <: Root, A : TypeKey](
  val path: String,
  op: EqualityOp,
  value: A)
extends Query[R] {
  val valTypeKey = typeKey[A]
}

/** an equality query where the left-hand side is a property */
sealed case class VEqualityQuery[R <: Root, A](
  val prop: Prop[R, A],
  op: EqualityOp,
  value: A)
extends ValidatedQuery[R]

/** an ordering query where the left-hand side is a property path */
sealed case class OrderingQuery[R <: Root, A : TypeKey](
  val path: String,
  op: OrderingOp,
  value: A)
extends Query[R] {
  val valTypeKey = typeKey[A]
}

/** an ordering query where the left-hand side is a property */
sealed case class VOrderingQuery[R <: Root, A](
  val prop: Prop[R, A],
  op: OrderingOp,
  value: A)
extends ValidatedQuery[R] {
  prop.ordering // force exception if prop is not ordered
}

/** a conditional query where one or both of the operands are non-validated queries */
sealed case class ConditionalQuery[R <: Root](
  lhs: Query[R],
  op: LogicalOp,
  rhs: Query[R])
extends Query[R]

/** a conditional query where both of the operands are validated queries */
sealed case class VConditionalQuery[R <: Root](
  lhs: ValidatedQuery[R],
  op: LogicalOp,
  rhs: ValidatedQuery[R])
extends ValidatedQuery[R]
