package longevity.subdomain.query

import longevity.subdomain.Persistent

/** a query for looking up persistent objects of type `P` */
case class Query[P <: Persistent](
  filter: QueryFilter[P],
  orderBy: QueryOrderBy[P] = QueryOrderBy.empty[P],
  offset: Option[Long] = None,
  limit: Option[Long] = None)
