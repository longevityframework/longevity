package longevity.integration

import emblem._
import longevity.domain._

/** covers a root entity with attributes of every supported basic type */
package object allAttributes {

  val entityTypes = EntityTypePool() + AllAttributes

  val boundedContext = BoundedContext("All Attributes", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
