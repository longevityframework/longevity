package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val uriShorthand = Shorthand[Uri, String]

  implicit val shorthandPool = ShorthandPool.empty + uriShorthand

  object context {
    val entityTypes = EntityTypePool() + OneShorthand
    val subdomain = Subdomain("One Shorthand", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}
