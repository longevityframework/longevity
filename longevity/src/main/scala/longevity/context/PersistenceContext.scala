package longevity.context

import longevity.persistence.Repo

/** the portion of a [[LongevityContext]] that deals with persistence
 * 
 * @tparam F the effect
 * @tparam M the model
 */
trait PersistenceContext[F[_], M] {

  /** the repository for this persistence context */
  val repo: Repo[F, M]

}
