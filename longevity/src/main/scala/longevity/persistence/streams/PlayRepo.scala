package longevity.persistence.streams

import emblem.TypeKey
import longevity.model.query.Query
import scala.concurrent.ExecutionContext
import play.api.libs.iteratee.Enumerator
import longevity.persistence.PState
import longevity.persistence.Repo

/** provides repository methods that use Play iteratees for repository streaming API.
 *
 * `PlayRepo` is provided by an implicit conversion from `Repo`, so that Play iteratees can remain
 * an optional dependency for longevity users. otherwise, it would have been included as part of
 * the [[Repo]].
 * 
 * @tparam M the model
 *
 * @param repo the repository
 */
class PlayRepo[M](repo: Repo[M]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToPlay[P : TypeKey](query: Query[P])(implicit context: ExecutionContext): Enumerator[PState[P]] =
    repo.pRepoMap[P].queryToPlayImpl(query)

}
