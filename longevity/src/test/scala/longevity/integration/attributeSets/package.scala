package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with set attributes for every supported basic type */
package object attributeSets {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeSets
    val subdomain = Subdomain("Attribute Sets", entityTypes, shorthandPool)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
