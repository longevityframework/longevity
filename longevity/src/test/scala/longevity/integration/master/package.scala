package longevity.integration

import emblem._
import longevity.domain._

/** covers everything found in the rest of the integration tests */
package object master {

  val entityTypes = EntityTypePool() +
    AllAttributes +
    OneAttribute +
    OneShorthand

  val uriShorthand = shorthandFor[Uri, String]

  val shorthandPool = ShorthandPool() + uriShorthand

  val boundedContext = BoundedContext("Master", entityTypes, shorthandPool)

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
