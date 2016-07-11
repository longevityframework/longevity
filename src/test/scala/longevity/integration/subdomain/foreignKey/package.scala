package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single association to another root entity */
package object foreignKey {
  val subdomain = Subdomain("Foreign Key", PTypePool(WithForeignKey, Associated))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)
}
