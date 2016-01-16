package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with list attributes for every supported basic type */
package object attributeLists {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + AttributeLists
    val subdomain = Subdomain("Attribute Lists", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
