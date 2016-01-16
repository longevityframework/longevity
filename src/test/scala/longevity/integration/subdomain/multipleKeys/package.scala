package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object multipleKeys {

  object context {
    val entityTypes = EntityTypePool() + MultipleKeys
    val subdomain = Subdomain("Multiple Nat Keys", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
