package longevity.subdomain.query

import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Prop

/** a query sort expression
 *
 * @param prop the property to sort on
 * @param direction the direction of the sort
 */
case class QuerySortExpr[P <: Persistent](
  prop: Prop[_ >: P <: Persistent, _],
  direction: QuerySortDirection = Ascending)
