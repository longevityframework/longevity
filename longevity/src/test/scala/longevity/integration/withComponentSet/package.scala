package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a set of component entities */
package object withComponentSet {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithComponentSet + Component
    val subdomain = Subdomain("With Component Set", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
