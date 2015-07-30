package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object attributeOptions {

  val entityTypes = EntityTypePool() + AttributeOptions

  val subdomain = Subdomain("Attribute Options", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
