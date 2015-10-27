package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object allAttributes {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + AllAttributes
    val subdomain = Subdomain("All Attributes", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
