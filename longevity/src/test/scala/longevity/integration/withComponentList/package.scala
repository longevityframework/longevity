package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single listal non-root entity */
package object withComponentList {

  val entityTypes = EntityTypePool() + WithComponentList + Component

  val subdomain = Subdomain("With Component List", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
