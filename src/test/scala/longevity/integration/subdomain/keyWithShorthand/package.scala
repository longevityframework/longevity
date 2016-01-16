package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object keyWithShorthand {

  object shorthands {
    val uriShorthand = Shorthand[Uri, String]
    implicit val shorthandPool = ShorthandPool.empty + uriShorthand
  }

  import shorthands._

  object context {
    val entityTypes = EntityTypePool() + KeyWithShorthand
    val subdomain = Subdomain("Nat Key With Shorthand", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
