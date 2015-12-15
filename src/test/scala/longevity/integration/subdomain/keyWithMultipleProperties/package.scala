package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a key that contains a shorthand */
package object keyWithMultipleProperties {

  object context {
    val entityTypes = EntityTypePool() + KeyWithMultipleProperties
    val subdomain = Subdomain("Key With Multiple Props", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
