package longevity.persistence.streams

import fs2.Stream
import fs2.Task
import longevity.model.query.Query
import longevity.persistence.PRepo
import longevity.persistence.PState
import longevity.persistence.OldRepo

/** provides repository methods that use FS2 for repository streaming API.
 *
 * `FS2Repo` is provided by an implicit conversion from `Repo`, so that FS2 can remain an optional
 * dependency for longevity users. otherwise, it would have been included as part of the [[OldRepo]].
 */
class FS2Repo[P](repo: OldRepo[P]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToFS2(query: Query[P]): Stream[Task, PState[P]] =
    repo.asInstanceOf[PRepo[P]].queryToFS2Impl(query)

}
