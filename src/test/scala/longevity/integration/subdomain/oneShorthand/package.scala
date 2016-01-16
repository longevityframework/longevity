package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single shorthand */
package object oneShorthand {

  object shorthands {
    val uriShorthand = Shorthand[Uri, String]
    implicit val shorthandPool = ShorthandPool.empty + uriShorthand
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + OneShorthand
    val subdomain = Subdomain("One Shorthand", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
