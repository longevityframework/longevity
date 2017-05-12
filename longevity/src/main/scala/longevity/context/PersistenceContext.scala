package longevity.context

import longevity.persistence.Repo

/** the portion of a [[LongevityContext]] that deals with persistence
 * 
 * @tparam M the model
 */
trait PersistenceContext[M] {

  /** a pool of the repositories for this persistence context */
  val repo: Repo[M]

}
