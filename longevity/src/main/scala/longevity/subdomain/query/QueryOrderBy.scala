package longevity.subdomain.query

import longevity.subdomain.ptype.Prop
import longevity.subdomain.realized.RealizedPType

/** a query order by clause
 * 
 * @param sortExprs the sort expressions that make up the order by clause
 */
case class QueryOrderBy[P](sortExprs: Seq[QuerySortExpr[P]])

/** contains a factory method for an empty order by clause */
object QueryOrderBy {

  /** an empty order by clause */
  def empty[P] = QueryOrderBy(Seq.empty[QuerySortExpr[P]])

  def ordering[P](orderBy: QueryOrderBy[P], realizedPType: RealizedPType[P]): scala.math.Ordering[P] = {
    val unitOrdering = new Ordering[P] { def compare(p1: P, p2: P) = 0 }
    orderBy.sortExprs.foldLeft(unitOrdering) { (ordering, sortExpr) =>
      new Ordering[P]() {

        def toRealized[A](prop: Prop[_ >: P, A]) = realizedPType.realizedProps(prop)

        def compare(p1: P, p2: P) = {
          val i = ordering.compare(p1, p2)
          if (i != 0) i else {
            val raw = toRealized(sortExpr.prop).pOrdering.compare(p1, p2)
            if (sortExpr.direction == Ascending) raw else raw * -1
          }
        }
      }
    }
  }

}
