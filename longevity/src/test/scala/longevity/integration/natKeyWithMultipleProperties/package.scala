package longevity.integration

import longevity.context._
import longevity.subdomain._

/** covers a root entity with a nat key that contains a shorthand */
package object natKeyWithMultipleProperties {

  val longShorthand = Shorthand[LongShorthand, Long]
  val uriShorthand = Shorthand[Uri, String]

  implicit val shorthandPool = ShorthandPool.empty + longShorthand + uriShorthand

  object context {
    val entityTypes = EntityTypePool() + NatKeyWithMultipleProperties + Associated
    val subdomain = Subdomain("Nat Key With Multiple Props", entityTypes, shorthandPool)
    val longevityContext = LongevityContext(subdomain)
  }

}
