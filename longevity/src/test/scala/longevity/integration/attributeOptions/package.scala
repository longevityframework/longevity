package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object attributeOptions {

  implicit val shorthandPool = ShorthandPool.empty

  val entityTypes = EntityTypePool() + AttributeOptions

  val subdomain = Subdomain("Attribute Options", entityTypes)

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}
