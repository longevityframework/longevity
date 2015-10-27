package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with option attributes for every supported basic type */
package object attributeOptions {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeOptions
    val subdomain = Subdomain("Attribute Options", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
