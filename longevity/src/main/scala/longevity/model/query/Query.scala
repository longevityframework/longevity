package longevity.model.query

/** a query for looking up persistent objects of type `P` */
case class Query[P](
  filter: QueryFilter[P],
  orderBy: QueryOrderBy[P] = QueryOrderBy.empty[P],
  offset: Option[Int] = None,
  limit: Option[Int] = None)
