package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an option of associations to another root entity */
package object withAssocOption {

  val entityTypes = EntityTypePool() + WithAssocOption + Associated

  val subdomain = Subdomain("With Assoc Option", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
