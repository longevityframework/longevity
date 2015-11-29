package longevity.subdomain

import emblem.imports._
import Query._

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
    DEqualityQuery[R, A](path, EqOp, value)

  def eqs[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SEqualityQuery[R, A](prop, EqOp, value)

  def neq[R <: RootEntity, A : TypeKey](path: String, value: A) =
    DEqualityQuery[R, A](path, NeqOp, value)

  def neq[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SEqualityQuery[R, A](prop, NeqOp, value)

  def lt[R <: RootEntity, A : TypeKey](path: String, value: A) =
    DOrderingQuery[R, A](path, LtOp, value)

  def lt[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SOrderingQuery[R, A](prop, LtOp, value)

  def gt[R <: RootEntity, A : TypeKey](path: String, value: A) =
    DOrderingQuery[R, A](path, GtOp, value)

  def gt[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SOrderingQuery[R, A](prop, GtOp, value)

  def lte[R <: RootEntity, A : TypeKey](path: String, value: A) =
    DOrderingQuery[R, A](path, LteOp, value)

  def lte[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SOrderingQuery[R, A](prop, LteOp, value)

  def gte[R <: RootEntity, A : TypeKey](path: String, value: A) =
    DOrderingQuery[R, A](path, GteOp, value)

  def gte[R <: RootEntity, A](prop: Prop[R, A], value: A) =
    SOrderingQuery[R, A](prop, GteOp, value)

  def and[R <: RootEntity](lhs: Query[R], rhs: Query[R], extras: Query[R]*) =
    extras.foldLeft(ConditionalQuery[R](lhs, AndOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[R](aggregate, AndOp, extra)
    }

  def or[R <: RootEntity](lhs: Query[R], rhs: Query[R], extras: Query[R]*) =
    extras.foldLeft(ConditionalQuery[R](lhs, OrOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[R](aggregate, OrOp, extra)
    }

}

sealed trait Query[R <: RootEntity] {
  val validated: Boolean
}

sealed trait EqualityQuery[R <: RootEntity] extends Query[R] {
  val op: EqualityOp
  val value: Any
}

sealed case class SEqualityQuery[R <: RootEntity, A](
  val prop: Prop[R, A],
  op: EqualityOp,
  value: A)
extends EqualityQuery[R] {
  val validated = true
}

sealed case class DEqualityQuery[R <: RootEntity, A : TypeKey](
  val path: String,
  op: EqualityOp,
  value: A)
extends EqualityQuery[R] {
  val valTypeKey = typeKey[A]
  val validated = false  
}

sealed trait OrderingQuery[R <: RootEntity, A] extends Query[R] {
  val op: OrderingOp
  val value: A
}

sealed case class SOrderingQuery[R <: RootEntity, A](
  val prop: Prop[R, A],
  op: OrderingOp,
  value: A)
extends OrderingQuery[R, A] {
  prop.ordering // force exception if prop is not ordered
  val validated = true
}

sealed case class DOrderingQuery[R <: RootEntity, A : TypeKey](
  val path: String,
  op: OrderingOp,
  value: A)
extends OrderingQuery[R, A] {
  val valTypeKey = typeKey[A]
  val validated = false  
}

sealed case class ConditionalQuery[R <: RootEntity](
  lhs: Query[R],
  op: LogicalOp,
  rhs: Query[R])
extends Query[R] {
  val validated = lhs.validated && rhs.validated
}
