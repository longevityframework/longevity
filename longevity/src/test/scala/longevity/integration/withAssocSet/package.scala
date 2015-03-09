package longevity.integration

import emblem._
import longevity.domain._

/** covers a root entity with an set of associations to another root entity */
package object withAssocSet {

  val entityTypes = EntityTypePool() + WithAssocSet + Associated

  val boundedContext = BoundedContext("With Assoc Set", entityTypes, ShorthandPool())

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
