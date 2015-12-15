package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a key that contains a shorthand */
package object multipleKeys {

  object context {
    val entityTypes = EntityTypePool() + MultipleKeys
    val subdomain = Subdomain("Multiple Keys", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
