package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single setal non-root entity */
package object withComponentSet {

  implicit val shorthandPool = ShorthandPool.empty

  val entityTypes = EntityTypePool() + WithComponentSet + Component

  val subdomain = Subdomain("With Component Set", entityTypes)

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}
