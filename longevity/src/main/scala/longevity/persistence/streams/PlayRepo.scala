package longevity.persistence.streams

import longevity.effect.Effect.Syntax
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
 * @tparam F the effect
 * @tparam M the model
 *
 * @param repo the repository
 */
class PlayRepo[F[_], M](repo: Repo[F, M]) {

  private implicit def implicitF = repo.implicitF

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToPlay[P: PEv[M, ?]](query: Query[P])(implicit context: ExecutionContext): F[Enumerator[PState[P]]] =
    for {
      pr <- repo.pRepoF[P]
      e  <- pr.queryToPlayImpl(query)
    } yield e

}
