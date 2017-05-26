package longevity.persistence.streams

import longevity.model.PEv
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.Repo
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext

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
  def queryToPlay[P: repo.PEvM](query: Query[P])(implicit context: ExecutionContext): Enumerator[PState[P]] =
    repo.pRepoMap(implicitly[PEv[M, P]].key).queryToPlayImpl(query)

}
