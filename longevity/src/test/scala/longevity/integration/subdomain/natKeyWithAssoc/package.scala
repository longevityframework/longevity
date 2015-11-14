package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object natKeyWithAssoc {

  val uriShorthand = Shorthand[Uri, String]

  implicit val shorthandPool = ShorthandPool.empty + uriShorthand

  object context {
    val entityTypes = EntityTypePool() + NatKeyWithAssoc + Associated
    val subdomain = Subdomain("Nat Key With Assoc", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
