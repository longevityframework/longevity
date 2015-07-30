package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an list of associations to another root entity */
package object withAssocList {

  implicit val shorthandPool = ShorthandPool.empty

  val entityTypes = EntityTypePool() + WithAssocList + Associated

  val subdomain = Subdomain("With Assoc List", entityTypes)

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}
