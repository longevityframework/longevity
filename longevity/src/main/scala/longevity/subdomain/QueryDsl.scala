package longevity.subdomain

import emblem.imports._
import Query._

// TODO scaladocs

class QueryDsl[R <: RootEntity] {

  implicit def where(path: String) = new GatherDRelational(path)
  implicit def where[A](prop: Prop[R, A]) = new GatherSRelational(prop)

  private[QueryDsl] case class CondPrefix(lhs: Query[R], op: LogicalOp) {
    def buildCond(rhs: Query[R]) = Query.cond(lhs, op, rhs)
  }

  class GatherDRelational private[QueryDsl] (
    private val path: String,
    private val prefix: Option[CondPrefix] = None) {

    def eqs[A : TypeKey](a: A) = {
      val rhs = DEqualityQuery[R, A](path, EqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq[A : TypeKey](a: A) = {
      val rhs = DEqualityQuery[R, A](path, NeqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[R, A](path, LtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[R, A](path, LteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[R, A](path, GtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte[A : TypeKey](a: A) = {
      val rhs = DOrderingQuery[R, A](path, GteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherSRelational[A] private[QueryDsl] (
    private val prop: Prop[R, A],
    private val prefix: Option[CondPrefix] = None) {

    def eqs(a: A) = {
      val rhs = SEqualityQuery[R, A](prop, EqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def neq(a: A) = {
      val rhs = SEqualityQuery[R, A](prop, NeqOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lt(a: A) = {
      val rhs = SOrderingQuery[R, A](prop, LtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def lte(a: A) = {
      val rhs = SOrderingQuery[R, A](prop, LteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gt(a: A) = {
      val rhs = SOrderingQuery[R, A](prop, GtOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

    def gte(a: A) = {
      val rhs = SOrderingQuery[R, A](prop, GteOp, a)
      val query = prefix.map(_.buildCond(rhs)).getOrElse(rhs)
      new GatherLogical(query)
    }

  }

  class GatherLogical private[QueryDsl] (private[QueryDsl] val prefix: Query[R]) {
    def and(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, AndOp)))
    def and[A](prop: Prop[R, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, AndOp)))
    def and(query: Query[R]) = new GatherLogical(Query.cond(prefix, AndOp, query))
    def or(path: String) = new GatherDRelational(path, Some(CondPrefix(prefix, OrOp)))
    def or[A](prop: Prop[R, A]) = new GatherSRelational(prop, Some(CondPrefix(prefix, OrOp)))
    def or(query: Query[R]) = new GatherLogical(Query.cond(prefix, OrOp, query))
  }

  implicit def query(gather: GatherLogical): Query[R] = gather.prefix

}
