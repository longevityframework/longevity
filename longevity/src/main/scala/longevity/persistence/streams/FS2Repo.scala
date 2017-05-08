package longevity.persistence.streams

import emblem.TypeKey
import fs2.Stream
import fs2.Task
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.Repo

/** provides repository methods that use FS2 for repository streaming API.
 *
 * `FS2Repo` is provided by an implicit conversion from `Repo`, so that FS2 can remain an optional
 * dependency for longevity users. otherwise, it would have been included as part of the [[Repo]].
 */
class FS2Repo(repo: Repo) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToFS2[P : TypeKey](query: Query[P]): Stream[Task, PState[P]] =
    repo.pRepoMap[P].queryToFS2Impl(query)

}
