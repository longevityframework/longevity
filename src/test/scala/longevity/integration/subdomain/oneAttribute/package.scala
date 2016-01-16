package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single attribute */
package object oneAttribute {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + OneAttribute
    val subdomain = Subdomain("One Attribute", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
