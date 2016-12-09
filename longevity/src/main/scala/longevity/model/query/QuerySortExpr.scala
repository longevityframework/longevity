package longevity.model.query

import longevity.model.ptype.Prop

/** a query sort expression
 *
 * @param prop the property to sort on
 * @param direction the direction of the sort
 */
case class QuerySortExpr[P](prop: Prop[_ >: P, _], direction: QuerySortDirection = Ascending)
