package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with option attributes for every supported basic type */
package object attributeOptions {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeOptions
    val subdomain = Subdomain("Attribute Options", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
