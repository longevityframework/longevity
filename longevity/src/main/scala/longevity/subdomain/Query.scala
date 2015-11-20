package longevity.subdomain

import emblem.imports._
import Query._

// TODO scaladocs

object Query {

  sealed trait RelationalOp
  case object EqOp extends RelationalOp
  case object NeqOp extends RelationalOp
  case object LtOp extends RelationalOp
  case object LteOp extends RelationalOp
  case object GtOp extends RelationalOp
  case object GteOp extends RelationalOp

  sealed trait LogicalOp
  case object AndOp extends LogicalOp
  case object OrOp extends LogicalOp

  def eqs[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, EqOp, value)

  def eqs[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, EqOp, value)

  def neq[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, NeqOp, value)

  def neq[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, NeqOp, value)

  def lt[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, LtOp, value)

  def lt[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, LtOp, value)

  def gt[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, GtOp, value)

  def gt[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, GtOp, value)

  def lte[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, LteOp, value)

  def lte[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, LteOp, value)

  def gte[E <: RootEntity, A : TypeKey](path: String, value: A) = DRelationalQuery[E, A](path, GteOp, value)

  def gte[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, GteOp, value)

  def and[E <: RootEntity](lhs: Query[E], rhs: Query[E], extras: Query[E]*) =
    extras.foldLeft(ConditionalQuery[E](lhs, AndOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[E](aggregate, AndOp, extra)
    }

  def or[E <: RootEntity](lhs: Query[E], rhs: Query[E], extras: Query[E]*) =
    extras.foldLeft(ConditionalQuery[E](lhs, OrOp, rhs)) {
      case (aggregate, extra) => ConditionalQuery[E](aggregate, OrOp, extra)
    }

}

sealed trait Query[E <: RootEntity]

sealed trait RelationalQuery[E <: RootEntity] extends Query[E] {
  val op: RelationalOp
  val value: Any
}

sealed case class SRelationalQuery[E <: RootEntity, A](
  val prop: Prop[E, A],
  op: RelationalOp,
  value: A)
extends RelationalQuery[E]

sealed case class DRelationalQuery[E <: RootEntity, A : TypeKey](
  val path: String,
  op: RelationalOp,
  value: A)
extends RelationalQuery[E] {
  val valTypeKey = typeKey[A]
}

sealed case class ConditionalQuery[E <: RootEntity](
  lhs: Query[E],
  op: LogicalOp,
  rhs: Query[E])
extends Query[E]
