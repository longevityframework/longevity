package longevity.integration

import emblem._
import longevity.domain._

/** covers a root entity with a single attribute */
package object oneAttribute {

  val entityTypes = EntityTypePool() + OneAttribute

  val boundedContext = BoundedContext("One Attribute", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
