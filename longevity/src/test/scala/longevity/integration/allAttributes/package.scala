package longevity.integration

import emblem._
import longevity.context._
import longevity.domain._

/** covers a root entity with attributes of every supported basic type */
package object allAttributes {

  val entityTypes = EntityTypePool() + AllAttributes

  val subdomain = Subdomain("All Attributes", entityTypes)

  val boundedContext = BoundedContext(Mongo, subdomain)

  val inMemRepoPool = longevity.repo.inMemRepoPool(subdomain)

  val mongoRepoPool = boundedContext.repoPool

}
