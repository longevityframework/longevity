package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.context.Cassandra
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with attributes of every supported basic type */
package object basics {

  val subdomain = Subdomain("Basics", PTypePool(Basics))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
