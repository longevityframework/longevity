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

  object shorthands {
    val uriShorthand = Shorthand[Uri, String]
    implicit val shorthandPool = ShorthandPool.empty + uriShorthand
  }

  import shorthands._

  object context {
    val subdomain = Subdomain("One Shorthand", PTypePool(OneShorthand))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
