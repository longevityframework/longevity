package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat index that contains a shorthand */
package object indexWithMultipleProperties {

  object context {
    val entityTypes = EntityTypePool() + IndexWithMultipleProperties
    val subdomain = Subdomain("Nat Index With Multiple Props", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
