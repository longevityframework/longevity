package longevity.subdomain.query

import longevity.subdomain.Persistent

/** a query order by clause
 * 
 * @param sortExprs the sort expressions that make up the order by clause
 */
case class QueryOrderBy[P <: Persistent](
  sortExprs: Seq[QuerySortExpr[P]])

/** contains a factory method for an empty order by clause */
object QueryOrderBy {

  /** an empty order by clause */
  def empty[P <: Persistent] = QueryOrderBy(Seq.empty[QuerySortExpr[P]])

}
