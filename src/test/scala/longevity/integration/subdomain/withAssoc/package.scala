package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a single association to another root */
package object withAssoc {

  object shorthands {
    implicit val shorthandPool = ShorthandPool.empty
  }

  import shorthands._

  val entityTypes = EntityTypePool() + WithAssoc + Associated
  val subdomain = Subdomain("With Assoc", entityTypes)
  val mongoContext = LongevityContext(subdomain, Mongo)

}
