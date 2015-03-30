package longevity.context

import longevity.persistence.RepoPool
import longevity.persistence.PersistenceStrategy
import longevity.persistence.SpecializedRepoFactoryPool

/** the portion of a [[LongevityContext]] that deals with persistence */
trait PersistenceContext {

  /** the persistence strategy used */
  val persistenceStrategy: PersistenceStrategy

  /** a collection factories for specialized repositories */
  val specializedRepoFactoryPool: SpecializedRepoFactoryPool // TODO name this better

  /** a pool of the repositories for this persistence context */
  val repoPool: RepoPool

}
