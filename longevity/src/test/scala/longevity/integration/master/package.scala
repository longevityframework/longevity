package longevity.integration

import emblem._
import longevity.domain._

/** covers everything found in the rest of the integration tests */
package object master {

  val entityTypes = EntityTypePool() + OneAttribute

  val boundedContext = BoundedContext("Master", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
