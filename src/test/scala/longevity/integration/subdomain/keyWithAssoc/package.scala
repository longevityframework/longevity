package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root with a key that contains a shorthand */
package object keyWithAssoc {

  val uriShorthand = Shorthand[Uri, String]

  implicit val shorthandPool = ShorthandPool.empty + uriShorthand

  object context {
    val entityTypes = EntityTypePool() + KeyWithAssoc + Associated
    val subdomain = Subdomain("Key With Assoc", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
