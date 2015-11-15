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

}

// so then KeyVal could be
// case class KeyVal[E <: RootEntity](query: EqQuery[E])

sealed trait Query[E <: RootEntity]

sealed trait EqQuery[E <: RootEntity] extends Query[E]

sealed trait BaseRelationalQuery[E <: RootEntity, V] extends Query[E] {
  val prop: Prop[E, V]
  val op: RelationalOp
  val value: V
}

case class RelationalQuery[E <: RootEntity, V](prop: Prop[E, V], op: RelationalOp, value: V)
extends BaseRelationalQuery[E, V]

case class EqRelationalQuery[E <: RootEntity, V](prop: Prop[E, V], value: V)
extends BaseRelationalQuery[E, V]
with EqQuery[E] {
  override val op = EqOp
}

sealed trait BaseCompoundQuery[E <: RootEntity] extends Query[E]

case class CompoundQuery[E <: RootEntity](
  lhs: Query[E],
  op: LogicalOp,
  rhs: Query[E])
extends BaseCompoundQuery[E]

case class EqCompoundQuery[E <: RootEntity](
  lhs: EqQuery[E],
  op: LogicalOp,
  rhs: EqQuery[E])
extends BaseCompoundQuery[E]
