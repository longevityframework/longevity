package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with an optional association to another root entity */
package object withAssocOption {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val subdomain = Subdomain("With Assoc Option", PTypePool(WithAssocOption, Associated))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
