package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a set of component entities */
package object withComponentSet {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithComponentSet + Component
    val subdomain = Subdomain("With Component Set", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
