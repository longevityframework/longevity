package longevity.subdomain

import emblem.imports._
import Query._

// TODO scaladocs

class QueryDsl[E <: RootEntity] {

  implicit def where(path: String) = new GatherDRelational(path)
  implicit def where[A](prop: Prop[E, A]) = new GatherSRelational(prop)

  private[QueryDsl] case class CondPrefix(lhs: Query[E], op: LogicalOp)

  class GatherDRelational private[QueryDsl] (
    private val path: String,
    private val prefix: Option[CondPrefix] = None) {

    def eqs[A : TypeKey](a: A) = {
      val rhs = DEqualityQuery[E, A](path, EqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq[A : TypeKey](a: A) = {
      val rhs = DEqualityQuery[E, A](path, NeqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[E, A](path, LtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[E, A](path, LteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[E, A](path, GtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[E, A](path, GteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherSRelational[A] private[QueryDsl] (
    private val prop: Prop[E, A],
    private val prefix: Option[CondPrefix] = None) {

    def eqs(a: A) = {
      val rhs = SEqualityQuery[E, A](prop, EqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq(a: A) = {
      val rhs = SEqualityQuery[E, A](prop, NeqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt(a: A) = {
      val rhs = SOrderingQuery[E, A](prop, LtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte(a: A) = {
      val rhs = SOrderingQuery[E, A](prop, LteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt(a: A) = {
      val rhs = SOrderingQuery[E, A](prop, GtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte(a: A) = {
      val rhs = SOrderingQuery[E, A](prop, GteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherLogical private[QueryDsl] (private[QueryDsl] val prefix: Query[E]) {
    def and(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, AndOp)))
    def and[A](prop: Prop[E, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, AndOp)))
    def and(query: Query[E]) = new GatherLogical(ConditionalQuery(prefix, AndOp, query))
    def or(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, OrOp)))
    def or[A](prop: Prop[E, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, OrOp)))
    def or(query: Query[E]) = new GatherLogical(ConditionalQuery(prefix, OrOp, query))
  }

  implicit def query(gather: GatherLogical): Query[E] = gather.prefix

}
