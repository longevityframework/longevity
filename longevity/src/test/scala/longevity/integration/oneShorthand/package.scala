package longevity.integration

import emblem._
import longevity.context._
import longevity.persistence._
import longevity.subdomain._

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val entityTypes = EntityTypePool() + OneShorthand

  val uriShorthand = Shorthand[Uri, String]

  val subdomain = Subdomain("One Shorthand", entityTypes)

  val shorthandPool = ShorthandPool.empty + uriShorthand

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}
