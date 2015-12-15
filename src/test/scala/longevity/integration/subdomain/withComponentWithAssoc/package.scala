package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a single component entity with an association to another root */
package object withComponentWithAssoc {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithComponentWithAssoc + ComponentWithAssoc + Associated
    val subdomain = Subdomain("With Component With Assoc", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
