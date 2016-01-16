package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single association to another root entity */
package object withAssoc {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithAssoc + Associated
    val subdomain = Subdomain("With Assoc", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
