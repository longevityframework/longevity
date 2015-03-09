package longevity.integration

import emblem._
import longevity.domain._

/** covers a root entity with a single association to another root entity */
package object withAssoc {

  val entityTypes = EntityTypePool() + WithAssoc + Associated

  val boundedContext = BoundedContext("With Assoc", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
