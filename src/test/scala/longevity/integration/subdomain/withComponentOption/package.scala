package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an optional component entity */
package object withComponentOption {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithComponentOption + Component
    val subdomain = Subdomain("With Component Option", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
