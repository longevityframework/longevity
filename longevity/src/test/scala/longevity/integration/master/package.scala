package longevity.integration

import emblem._
import longevity.domain._
import longevity.repo._

package object master {

  val entityTypes = EntityTypePool() + UserType

  val boundedContext = BoundedContext("Master", entityTypes, ShorthandPool())

  val inMemRepoPool = inMemRepoPoolForBoundedCountext(boundedContext)

  val mongoRepoPool = mongoRepoPoolForBoundedCountext(boundedContext)

}
