package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object attributeSets {

  val entityTypes = EntityTypePool() + AttributeSets

  val subdomain = Subdomain("Attribute Sets", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
