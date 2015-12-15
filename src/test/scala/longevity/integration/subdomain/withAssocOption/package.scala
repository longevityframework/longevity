package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with an optional association to another root */
package object withAssocOption {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithAssocOption + Associated
    val subdomain = Subdomain("With Assoc Option", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
