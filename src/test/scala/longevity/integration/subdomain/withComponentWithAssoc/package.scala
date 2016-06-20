package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity with an association to another root entity */
package object withComponentWithAssoc {

  val subdomain = Subdomain(
    "With Component With Assoc",
    PTypePool(WithComponentWithAssoc, Associated),
    ETypePool(ComponentWithAssoc))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
