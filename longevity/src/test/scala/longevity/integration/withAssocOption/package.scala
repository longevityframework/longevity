package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with an optional association to another root entity */
package object withAssocOption {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithAssocOption + Associated
    val subdomain = Subdomain("With Assoc Option", entityTypes, shorthandPool)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
