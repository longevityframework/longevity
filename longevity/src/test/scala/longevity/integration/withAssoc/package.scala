package longevity.integration

import emblem._
import longevity.context._
import longevity.domain._

/** covers a root entity with a single association to another root entity */
package object withAssoc {

  val entityTypes = EntityTypePool() + WithAssoc + Associated

  val subdomain = Subdomain("With Assoc", entityTypes)

  val boundedContext = BoundedContext(Mongo, subdomain)

  val inMemRepoPool = longevity.repo.inMemRepoPool(subdomain)

  val mongoRepoPool = boundedContext.repoPool

}
