package longevity.context

import longevity.persistence.RepoPool

/** the portion of a [[LongevityContext]] that deals with persistence */
trait PersistenceContext {

  /** a pool of the repositories for this persistence context */
  val repoPool: RepoPool

}
