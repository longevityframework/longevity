package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object multipleNatKeys {

  object context {
    val entityTypes = EntityTypePool() + MultipleNatKeys
    val subdomain = Subdomain("Multiple Nat Keys", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
