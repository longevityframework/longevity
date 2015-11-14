package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object natKeyWithMultipleProperties {

  object context {
    val entityTypes = EntityTypePool() + NatKeyWithMultipleProperties
    val subdomain = Subdomain("Nat Key With Multiple Props", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
