package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single component entity */
package object withComponent {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithComponent + Component
    val subdomain = Subdomain("With Component", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
