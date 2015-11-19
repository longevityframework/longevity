package longevity.subdomain

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

  // TODO: vanilla API and DSL for SRelationalQuery

  def eqs[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, EqOp, value)
  def eqs[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, EqOp, value)
  def neq[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, NeqOp, value)
  def neq[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, NeqOp, value)
  def lt[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, LtOp, value)
  def lt[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, LtOp, value)
  def gt[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, GtOp, value)
  def gt[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, GtOp, value)
  def lte[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, LteOp, value)
  def lte[E <: RootEntity, A](prop: Prop[E, A], value: A) = SRelationalQuery[E, A](prop, LteOp, value)
  def gte[E <: RootEntity](path: String, value: Any) = DRelationalQuery[E](path, GteOp, value)
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

sealed case class DRelationalQuery[E <: RootEntity](
  val path: String,
  op: RelationalOp,
  value: Any)
extends RelationalQuery[E]

sealed case class ConditionalQuery[E <: RootEntity](
  lhs: Query[E],
  op: LogicalOp,
  rhs: Query[E])
extends Query[E]
