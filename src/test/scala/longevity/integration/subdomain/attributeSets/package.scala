package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with set attributes for every supported basic type */
package object attributeSets {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeSets
    val subdomain = Subdomain("Attribute Sets", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
