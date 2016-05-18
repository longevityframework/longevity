package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val subdomain = Subdomain(
    "One Shorthand",
    PTypePool(OneShorthand),
    EntityTypePool(),
    ShorthandPool(Uri))

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
