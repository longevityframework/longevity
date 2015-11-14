package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a set of associations to another root entity */
package object withAssocSet {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithAssocSet + Associated
    val subdomain = Subdomain("With Assoc Set", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
