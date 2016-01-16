package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single component entity with an association to another root entity */
package object withComponentWithAssoc {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + WithComponentWithAssoc + ComponentWithAssoc + Associated
    val subdomain = Subdomain("With Component With Assoc", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
