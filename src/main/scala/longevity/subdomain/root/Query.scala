package longevity.subdomain.root

import Query._
import emblem.imports._
import longevity.subdomain._

/** query operators and factory methods */
object Query {

  /** those relational operators - namely, _equals_ and _not equals_ - that
   * apply, regardless of the types of the operands. this is in contrast to
   * relational operators such _greater than_, which only apply to types that
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

  /** either of the binary logical operators _and_ and _or_ */
  sealed trait LogicalOp

  /** the and operator */
  case object AndOp extends LogicalOp

  /** the or operator */
  case object OrOp extends LogicalOp

  // TODO remove deprecated

  /** a factory method for producing an [[EqualityQuery]] with an [[EqOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def eqs[P <: Persistent, A : TypeKey](path: String, value: A) =
    EqualityQuery[P, A](path, EqOp, value)

  /** a factory method for producing a [[VEqualityQuery]] with an [[EqOp]] */
  def eqs[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VEqualityQuery[P, A](prop, EqOp, value)

  /** a factory method for producing an [[EqualityQuery]] with an [[NeqOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def neq[P <: Persistent, A : TypeKey](path: String, value: A) =
    EqualityQuery[P, A](path, NeqOp, value)

  /** a factory method for producing a [[VEqualityQuery]] with an [[NeqOp]] */
  def neq[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VEqualityQuery[P, A](prop, NeqOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[LtOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def lt[P <: Persistent, A : TypeKey](path: String, value: A) =
    OrderingQuery[P, A](path, LtOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LtOp]] */
  def lt[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VOrderingQuery[P, A](prop, LtOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[LteOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def lte[P <: Persistent, A : TypeKey](path: String, value: A) =
    OrderingQuery[P, A](path, LteOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LteOp]] */
  def lte[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VOrderingQuery[P, A](prop, LteOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[GtOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def gt[P <: Persistent, A : TypeKey](path: String, value: A) =
    OrderingQuery[P, A](path, GtOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[GtOp]] */
  def gt[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VOrderingQuery[P, A](prop, GtOp, value)

  /** a factory method for producing an [[OrderingQuery]] with a [[GteOp]] */
  @deprecated("use the alternative method that takes a prop", "0.5")
  def gte[P <: Persistent, A : TypeKey](path: String, value: A) =
    OrderingQuery[P, A](path, GteOp, value)

  /** a factory method for producing a [[VOrderingQuery]] with a [[LteOp]] */
  def gte[P <: Persistent, A](prop: Prop[P, A], value: A) =
    VOrderingQuery[P, A](prop, GteOp, value)

  /** a factory method for producing a conditional [[Query]] with an [[AndOp]] */
  def and[P <: Persistent](lhs: Query[P], rhs: Query[P]) = cond(lhs, AndOp, rhs)

  /** a factory method for producing a conditional [[Query]] with an [[OrOp]] */
  def or[P <: Persistent](lhs: Query[P], rhs: Query[P]) = cond(lhs, OrOp, rhs)

  private[root] def cond[P <: Persistent](lhs: Query[P], op: LogicalOp, rhs: Query[P]) = (lhs, rhs) match {
    case (lhs: ValidatedQuery[P], rhs: ValidatedQuery[P]) => VConditionalQuery[P](lhs, op, rhs)
    case _ => ConditionalQuery[P](lhs, op, rhs)
  }

}

/** a query for looking up aggregates of type `R` */
sealed trait Query[P <: Persistent]

/** a query where all the types check out */
sealed trait ValidatedQuery[P <: Persistent] extends Query[P]

/** an equality query where the left-hand side is a property path */
sealed case class EqualityQuery[P <: Persistent, A : TypeKey](
  val path: String,
  op: EqualityOp,
  value: A)
extends Query[P] {
  val valTypeKey = typeKey[A]
}

/** an equality query where the left-hand side is a property */
sealed case class VEqualityQuery[P <: Persistent, A](
  val prop: Prop[P, A],
  op: EqualityOp,
  value: A)
extends ValidatedQuery[P]

/** an ordering query where the left-hand side is a property path */
sealed case class OrderingQuery[P <: Persistent, A : TypeKey](
  val path: String,
  op: OrderingOp,
  value: A)
extends Query[P] {
  val valTypeKey = typeKey[A]
}

/** an ordering query where the left-hand side is a property */
sealed case class VOrderingQuery[P <: Persistent, A](
  val prop: Prop[P, A],
  op: OrderingOp,
  value: A)
extends ValidatedQuery[P] {
  prop.ordering // force exception if prop is not ordered
}

/** a conditional query where one or both of the operands are non-validated queries */
sealed case class ConditionalQuery[P <: Persistent](
  lhs: Query[P],
  op: LogicalOp,
  rhs: Query[P])
extends Query[P]

/** a conditional query where both of the operands are validated queries */
sealed case class VConditionalQuery[P <: Persistent](
  lhs: ValidatedQuery[P],
  op: LogicalOp,
  rhs: ValidatedQuery[P])
extends ValidatedQuery[P]
