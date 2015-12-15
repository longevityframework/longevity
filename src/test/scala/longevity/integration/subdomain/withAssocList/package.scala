package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a list of associations to another root */
package object withAssocList {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithAssocList + Associated
    val subdomain = Subdomain("With Assoc List", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
