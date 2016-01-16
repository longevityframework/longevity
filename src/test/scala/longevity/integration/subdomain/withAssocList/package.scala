package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a list of associations to another root entity */
package object withAssocList {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithAssocList + Associated
    val subdomain = Subdomain("With Assoc List", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
