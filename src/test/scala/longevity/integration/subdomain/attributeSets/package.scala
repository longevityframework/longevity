package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with set attributes for every supported basic type */
package object attributeSets {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + AttributeSets
    val subdomain = Subdomain("Attribute Sets", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
