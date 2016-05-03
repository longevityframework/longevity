package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.EntityTypePool
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a set of component entities */
package object withComponentSet {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val subdomain = Subdomain("With Component Set", PTypePool(WithComponentSet), EntityTypePool(Component))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
