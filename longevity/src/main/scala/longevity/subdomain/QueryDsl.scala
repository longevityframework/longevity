package longevity.subdomain

import Query._

// TODO scaladocs

class QueryDsl[E <: RootEntity] {

  implicit def select(path: String): GatherRelational = new GatherRelational(path)

  private[QueryDsl] case class CondPrefix(lhs: Query[E], op: LogicalOp)

  class GatherRelational private[QueryDsl] (
    private val path: String,
    private val prefix: Option[CondPrefix] = None) {

    def eqs[A](a: A) = {
      val rhs = DRelationalQuery[E](path, EqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt[A](a: A) = {
      val rhs = new DRelationalQuery[E](path, LtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt[A](a: A) = {
      val rhs = new DRelationalQuery[E](path, GtOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq[A](a: A) = {
      val rhs = new DRelationalQuery[E](path, NeqOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte[A](a: A) = {
      val rhs = new DRelationalQuery[E](path, LteOp, a)
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte[A](a: A) = {
      val rhs = new DRelationalQuery[E](path, GteOp, a)    
      val query = prefix.map(p => ConditionalQuery(p.lhs, p.op, rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherLogical private[QueryDsl] (private val prefix: Query[E]) {
    def and(path: String) = new GatherRelational(path, Some(CondPrefix(prefix, AndOp)))
    def or(path: String) = new GatherRelational(path, Some(CondPrefix(prefix, OrOp)))
  }

  implicit def query[E <: RootEntity](gather: GatherLogical): Query[E] =
    ???

}
