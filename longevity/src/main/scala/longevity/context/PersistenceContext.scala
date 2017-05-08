package longevity.context

import longevity.persistence.Repo

/** the portion of a [[LongevityContext]] that deals with persistence */
trait PersistenceContext {

  /** a pool of the repositories for this persistence context */
  val repo: Repo

}
