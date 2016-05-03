package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a key that contains a shorthand */
package object keyWithMultipleProperties {

  object context {
    val subdomain = Subdomain("Key With Multiple Props", PTypePool(KeyWithMultipleProperties))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
