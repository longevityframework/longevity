package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a set of component entities */
package object withComponentSet {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithComponentSet + Component
    val subdomain = Subdomain("With Component Set", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
