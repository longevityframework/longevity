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

  def eqs[E <: RootEntity, A : TypeKey](path: String, value: A) =
    DEqualityQuery[E, A](path, EqOp, value)

  def eqs[E <: RootEntity, A](prop: Prop[E, A], value: A) =
    SEqualityQuery[E, A](prop, EqOp, value)

  def neq[E <: RootEntity, A : TypeKey](path: String, value: A) =
    DEqualityQuery[E, A](path, NeqOp, value)

  def neq[E <: RootEntity, A](prop: Prop[E, A], value: A) =
    SEqualityQuery[E, A](prop, NeqOp, value)

  def lt[E <: RootEntity, A : TypeKey](path: String, value: A)(implicit ordering: Ordering[A]) =
    DOrderingQuery[E, A](path, LtOp, value)(ordering)

  def lt[E <: RootEntity, A](prop: Prop[E, A], value: A)(implicit ordering: Ordering[A]) =
    SOrderingQuery[E, A](prop, LtOp, value)(ordering)

  def gt[E <: RootEntity, A : TypeKey](path: String, value: A)(implicit ordering: Ordering[A]) =
    DOrderingQuery[E, A](path, GtOp, value)(ordering)

  def gt[E <: RootEntity, A](prop: Prop[E, A], value: A)(implicit ordering: Ordering[A]) =
    SOrderingQuery[E, A](prop, GtOp, value)(ordering)

  def lte[E <: RootEntity, A : TypeKey](path: String, value: A)(implicit ordering: Ordering[A]) =
    DOrderingQuery[E, A](path, LteOp, value)(ordering)

  def lte[E <: RootEntity, A](prop: Prop[E, A], value: A)(implicit ordering: Ordering[A]) =
    SOrderingQuery[E, A](prop, LteOp, value)(ordering)

  def gte[E <: RootEntity, A : TypeKey](path: String, value: A)(implicit ordering: Ordering[A]) =
    DOrderingQuery[E, A](path, GteOp, value)(ordering)

  def gte[E <: RootEntity, A](prop: Prop[E, A], value: A)(implicit ordering: Ordering[A]) =
    SOrderingQuery[E, A](prop, GteOp, value)(ordering)

  def and[E <: RootEntity](lhs: Query[E], rhs: Query[E], extras: Query[E]*) =
    extras.foldLeft(ConditionalQuery[E](lhs, AndOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[E](aggregate, AndOp, extra)
    }

  def or[E <: RootEntity](lhs: Query[E], rhs: Query[E], extras: Query[E]*) =
    extras.foldLeft(ConditionalQuery[E](lhs, OrOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[E](aggregate, OrOp, extra)
    }

}

sealed trait Query[E <: RootEntity] {
  val validated: Boolean
}

sealed trait EqualityQuery[E <: RootEntity] extends Query[E] {
  val op: EqualityOp
  val value: Any
}

sealed case class SEqualityQuery[E <: RootEntity, A](
  val prop: Prop[E, A],
  op: EqualityOp,
  value: A)
extends EqualityQuery[E] {
  val validated = true
}

sealed case class DEqualityQuery[E <: RootEntity, A : TypeKey](
  val path: String,
  op: EqualityOp,
  value: A)
extends EqualityQuery[E] {
  val valTypeKey = typeKey[A]
  val validated = false  
}

sealed trait OrderingQuery[E <: RootEntity, A] extends Query[E] {
  val op: OrderingOp
  val value: A
  val ordering: Ordering[A]
}

sealed case class SOrderingQuery[E <: RootEntity, A](
  val prop: Prop[E, A],
  op: OrderingOp,
  value: A)(
  val ordering: Ordering[A])
extends OrderingQuery[E, A] {
  val validated = true
}

sealed case class DOrderingQuery[E <: RootEntity, A : TypeKey](
  val path: String,
  op: OrderingOp,
  value: A)(
  val ordering: Ordering[A])
extends OrderingQuery[E, A] {
  val valTypeKey = typeKey[A]
  val validated = false  
}

sealed case class ConditionalQuery[E <: RootEntity](
  lhs: Query[E],
  op: LogicalOp,
  rhs: Query[E])
extends Query[E] {
  val validated = lhs.validated && rhs.validated
}
