package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single optional non-root entity */
package object withComponentOption {

  val entityTypes = EntityTypePool() + WithComponentOption + Component

  val subdomain = Subdomain("With Component Option", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
