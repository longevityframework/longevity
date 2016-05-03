package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single attribute */
package object oneAttribute {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val subdomain = Subdomain("One Attribute", PTypePool(OneAttribute))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
