package musette

import longevity.repo._
import musette.domain.boundedContext

package object repo {

  val inMemRepoPool = inMemRepoPoolForBoundedCountext(boundedContext)

  val mongoRepoPool = mongoRepoPoolForBoundedCountext(boundedContext)

}
