package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with option attributes for every supported basic type */
package object attributeOptions {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + AttributeOptions
    val subdomain = Subdomain("Attribute Options", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
