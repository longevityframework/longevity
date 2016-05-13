package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val uriShorthand = Shorthand[Uri, String]

  val subdomain = Subdomain(
    "One Shorthand",
    PTypePool(OneShorthand),
    shorthandPool = ShorthandPool(uriShorthand))

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
