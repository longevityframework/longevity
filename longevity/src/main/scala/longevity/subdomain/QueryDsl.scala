package longevity.subdomain

import Query._

// TODO scaladocs

class QueryDsl[E <: RootEntity] {

  implicit def where(path: String) = new GatherDRelational(path)
  implicit def where[A](prop: Prop[E, A]) = new GatherSRelational(prop)

  private[QueryDsl] case class CondPrefix(lhs: Query[E], op: LogicalOp)

  class GatherDRelational private[QueryDsl] (
    private val path: String,
    private val prefix: Option[CondPrefix] = None) {

    def eqs[A](a: A) = {
      val rhs = DRelationalQuery[E](path, EqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt[A](a: A) = {
      val rhs = DRelationalQuery[E](path, LtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt[A](a: A) = {
      val rhs = DRelationalQuery[E](path, GtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq[A](a: A) = {
      val rhs = DRelationalQuery[E](path, NeqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte[A](a: A) = {
      val rhs = DRelationalQuery[E](path, LteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte[A](a: A) = {
      val rhs = DRelationalQuery[E](path, GteOp, a)    
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherSRelational[A] private[QueryDsl] (
    private val prop: Prop[E, A],
    private val prefix: Option[CondPrefix] = None) {

    def eqs(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, EqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, LtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, GtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, NeqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, LteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte(a: A) = {
      val rhs = SRelationalQuery[E, A](prop, GteOp, a)    
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
