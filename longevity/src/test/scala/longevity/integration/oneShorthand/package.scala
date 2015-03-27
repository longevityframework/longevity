package longevity.integration

import emblem._
import longevity.context._
import longevity.persistence._
import longevity.subdomain._

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val entityTypes = EntityTypePool() + OneShorthand

  val uriShorthand = shorthandFor[Uri, String]

  val subdomain = Subdomain("One Shorthand", entityTypes)

  val shorthandPool = ShorthandPool() + uriShorthand

  val longevityContext = LongevityContext(Mongo, subdomain, shorthandPool)

}
