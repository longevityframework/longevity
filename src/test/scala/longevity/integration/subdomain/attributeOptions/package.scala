package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with option attributes for every supported basic type */
package object attributeOptions {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  object context {
    val subdomain = Subdomain("Attribute Options", PTypePool(AttributeOptions))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
