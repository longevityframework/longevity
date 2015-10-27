package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single association to another root entity */
package object withAssoc {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithAssoc + Associated
    val subdomain = Subdomain("With Assoc", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
