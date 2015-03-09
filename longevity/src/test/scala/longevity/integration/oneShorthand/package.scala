package longevity.integration

import emblem._
import longevity.domain._

/** covers a root entity with a single shorthand */
package object oneShorthand {

  val entityTypes = EntityTypePool() + OneShorthand

  val uriShorthand = shorthandFor[Uri, String]

  val shorthandPool = ShorthandPool() + uriShorthand

  val boundedContext = BoundedContext("One Shorthand", entityTypes, shorthandPool)

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
