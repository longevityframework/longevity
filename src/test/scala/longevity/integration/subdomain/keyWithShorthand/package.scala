package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a key that contains a shorthand */
package object keyWithShorthand {

  val uriShorthand = Shorthand[Uri, String]

  implicit val shorthandPool = ShorthandPool.empty + uriShorthand

  object context {
    val entityTypes = EntityTypePool() + KeyWithShorthand
    val subdomain = Subdomain("Key With Shorthand", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
