package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an optional component entity */
package object withComponentOption {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithComponentOption + Component
    val subdomain = Subdomain("With Component Option", entityTypes, shorthandPool)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
