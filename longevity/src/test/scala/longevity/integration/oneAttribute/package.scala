package longevity.integration

import longevity.context._
import longevity.persistence._
import longevity.shorthands._
import longevity.subdomain._

/** covers a root entity with a single attribute */
package object oneAttribute {

  val entityTypes = EntityTypePool() + OneAttribute

  val subdomain = Subdomain("One Attribute", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool.empty, Mongo)

}
