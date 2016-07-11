package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with set attributes for every supported basic type */
package object basicSets {

  val subdomain = Subdomain("Basic Sets", PTypePool(BasicSets))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
