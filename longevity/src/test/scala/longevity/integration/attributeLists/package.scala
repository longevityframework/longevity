package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with list attributes for every supported basic type */
package object attributeLists {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AttributeLists
    val subdomain = Subdomain("Attribute Lists", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
