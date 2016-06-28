package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single shorthand */
package object withMultiPropComponent {

  val subdomain = Subdomain(
    "With Single Prop Component",
    PTypePool(WithMultiPropComponent),
    ETypePool(MultiPropComponent))

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
