package longevity.subdomain.ptype

import longevity.subdomain.Persistent

/** a query for looking up persistent objects of type `P` */
case class Query[P <: Persistent](
  filter: QueryFilter[P])
