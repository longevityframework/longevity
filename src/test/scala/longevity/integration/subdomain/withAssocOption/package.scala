package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an optional association to another root entity */
package object withAssocOption {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithAssocOption + Associated
    val subdomain = Subdomain("With Assoc Option", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
