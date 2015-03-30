package longevity.context

import emblem.traversors.Generator.CustomGeneratorPool
import longevity.persistence.RepoPool

/** the portion of a [[LongevityContext]] that deals with testing */
trait TestContext {

  /** a collection of custom generators to use when generating test data. defaults to an empty collection */
  val customGeneratorPool: CustomGeneratorPool

  /** An in-memory set of repositories for this longevity context, for use in testing. at the moment, no
   * specializedRepoFactoryPool are provided.
   */
  val inMemRepoPool: RepoPool

}
