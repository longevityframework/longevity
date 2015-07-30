package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object attributeLists {

  val entityTypes = EntityTypePool() + AttributeLists

  val subdomain = Subdomain("Attribute Lists", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
