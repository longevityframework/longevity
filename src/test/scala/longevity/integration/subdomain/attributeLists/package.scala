package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with list attributes for every supported basic type */
package object attributeLists {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeLists
    val subdomain = Subdomain("Attribute Lists", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
