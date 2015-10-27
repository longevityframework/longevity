package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single component entity with an association to another root entity */
package object withComponentWithAssoc {

  implicit val shorthandPool = ShorthandPool.empty

  object context {
    val entityTypes = EntityTypePool() + WithComponentWithAssoc + ComponentWithAssoc + Associated
    val subdomain = Subdomain("With Component With Assoc", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
