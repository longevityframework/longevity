package longevity.persistence.streams

import fs2.Stream
import fs2.Task
import longevity.model.PEv
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.Repo

/** provides repository methods that use FS2 for repository streaming API.
 *
 * `FS2Repo` is provided by an implicit conversion from `Repo`, so that FS2 can remain an optional
 * dependency for longevity users. otherwise, it would have been included as part of the [[Repo]].
 * 
 * @tparam M the model
 */
class FS2Repo[M](repo: Repo[M]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToFS2[P: repo.PEvM](query: Query[P]): Stream[Task, PState[P]] =
    repo.pRepoMap(implicitly[PEv[M, P]].key).queryToFS2Impl(query)

}
