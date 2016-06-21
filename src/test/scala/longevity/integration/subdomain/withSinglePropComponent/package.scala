package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single shorthand */
package object withSinglePropComponent {

  val subdomain = Subdomain(
    "One Shorthand",
    PTypePool(WithSinglePropComponent),
    ETypePool(),
    ShorthandPool(Uri))

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
