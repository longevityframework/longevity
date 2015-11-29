package longevity.subdomain.root

import Query._
import emblem.imports._
import longevity.subdomain._

// TODO scaladocs

object Query {

  sealed trait EqualityOp
  case object EqOp extends EqualityOp
  case object NeqOp extends EqualityOp

  sealed trait OrderingOp
  case object LtOp extends OrderingOp
  case object LteOp extends OrderingOp
  case object GtOp extends OrderingOp
  case object GteOp extends OrderingOp

  sealed trait LogicalOp
  case object AndOp extends LogicalOp
  case object OrOp extends LogicalOp

  def eqs[R <: RootEntity, A : TypeKey](path: String, value: A) =
    EqualityQuery[R, A](path, EqOp, value)

  def eqs[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VEqualityQuery[R, A](prop, EqOp, value)

  def neq[R <: RootEntity, A : TypeKey](path: String, value: A) =
    EqualityQuery[R, A](path, NeqOp, value)

  def neq[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VEqualityQuery[R, A](prop, NeqOp, value)

  def lt[R <: RootEntity, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, LtOp, value)

  def lt[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, LtOp, value)

  def gt[R <: RootEntity, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, GtOp, value)

  def gt[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, GtOp, value)

  def lte[R <: RootEntity, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, LteOp, value)

  def lte[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, LteOp, value)

  def gte[R <: RootEntity, A : TypeKey](path: String, value: A) =
    OrderingQuery[R, A](path, GteOp, value)

  def gte[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    VOrderingQuery[R, A](prop, GteOp, value)

  def cond[R <: RootEntity](lhs: Query[R], op: LogicalOp, rhs: Query[R]) = (lhs, rhs) match {
    case (lhs: ValidatedQuery[R], rhs: ValidatedQuery[R]) => VConditionalQuery[R](lhs, op, rhs)
    case _ => ConditionalQuery[R](lhs, op, rhs)
  }

  def and[R <: RootEntity](lhs: Query[R], rhs: Query[R]) = cond(lhs, AndOp, rhs)

  def or[R <: RootEntity](lhs: Query[R], rhs: Query[R]) = cond(lhs, OrOp, rhs)

}

sealed trait Query[R <: RootEntity]

// TODO alias VQuery
sealed trait ValidatedQuery[R <: RootEntity] extends Query[R]

sealed case class EqualityQuery[R <: RootEntity, A : TypeKey](
  val path: String,
  op: EqualityOp,
  value: A)
extends Query[R] {
  val valTypeKey = typeKey[A]
}

sealed case class VEqualityQuery[R <: RootEntity, A](
  val prop: Prop[R, A],
  op: EqualityOp,
  value: A)
extends ValidatedQuery[R]

sealed case class OrderingQuery[R <: RootEntity, A : TypeKey](
  val path: String,
  op: OrderingOp,
  value: A)
extends Query[R] {
  val valTypeKey = typeKey[A]
}

sealed case class VOrderingQuery[R <: RootEntity, A](
  val prop: Prop[R, A],
  op: OrderingOp,
  value: A)
extends ValidatedQuery[R] {
  prop.ordering // force exception if prop is not ordered
}

sealed case class ConditionalQuery[R <: RootEntity](
  lhs: Query[R],
  op: LogicalOp,
  rhs: Query[R])
extends Query[R]

sealed case class VConditionalQuery[R <: RootEntity](
  lhs: ValidatedQuery[R],
  op: LogicalOp,
  rhs: ValidatedQuery[R])
extends ValidatedQuery[R]
