package longevity.persistence

import longevity.model.query.Query
import scala.concurrent.ExecutionContext
import play.api.libs.iteratee.Enumerator

/** provides repository methods that use Play iteratees for repository streaming API.
 *
 * `PlayRepo` is provided by an implicit conversion from `Repo`, so that Play iteratees can remain
 * an optional dependency for longevity users. otherwise, it would have been included as part of
 * the [[Repo]].
 */
class PlayRepo[P](repo: Repo[P]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToPlay[F[_]](query: Query[P])(implicit context: ExecutionContext): Enumerator[PState[P]] =
    repo.asInstanceOf[BaseRepo[P]].queryToPlayImpl(query)

}
