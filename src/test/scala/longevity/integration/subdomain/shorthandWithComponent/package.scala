package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity */
package object shorthandWithComponent {

  val subdomain = Subdomain(
    "Shorthand With Component",
    PTypePool(WithShorthandWithComponent),
    ETypePool(ShorthandWithComponent, Component))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
