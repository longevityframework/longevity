package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object allAttributes {

  object shorthands {
    implicit val pool = ShorthandPool.empty
  }

  import shorthands._

  val entityTypes = EntityTypePool() + AllAttributes
  val subdomain = Subdomain("All Attributes", entityTypes)
  val mongoContext = LongevityContext(subdomain, Mongo)

}
