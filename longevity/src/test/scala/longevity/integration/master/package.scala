package longevity.integration

import emblem._
import longevity.domain._

package object master {

  val entityTypes = EntityTypePool() + UserType

  val boundedContext = BoundedContext("Master", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
