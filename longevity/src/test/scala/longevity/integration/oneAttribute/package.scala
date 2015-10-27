package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single attribute */
package object oneAttribute {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + OneAttribute
    val subdomain = Subdomain("One Attribute", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
