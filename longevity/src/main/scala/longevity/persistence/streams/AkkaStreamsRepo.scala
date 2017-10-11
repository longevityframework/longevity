package longevity.persistence.streams

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.effect.Effect.Syntax
import longevity.model.PEv
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.Repo

/** provides repository methods that use Akka Streams for repository streaming API.
 *
 * `AkkaStreamsRepo` is provided by an implicit conversion from `Repo`, so that
 * Akka Streams can remain an optional dependency for longevity users.
 * otherwise, it would have been included as part of the [[Repo]].
 * 
 * @tparam F the effect
 * @tparam M the model
 */
class AkkaStreamsRepo[F[_], M](repo: Repo[F, M]) {

  private implicit def implicitF = repo.implicitF

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToAkkaStream[P: PEv[M, ?]](query: Query[P]): F[Source[PState[P], NotUsed]] = for {
    pr <- repo.pRepoF[P]
    as <- pr.queryToAkkaStreamImpl(query)
  } yield as

}
