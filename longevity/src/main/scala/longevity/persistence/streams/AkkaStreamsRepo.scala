package longevity.persistence.streams

import emblem.TypeKey
import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.RepoPool

/** provides repository methods that use Akka Streams for repository streaming
 * API.
 *
 * `AkkaStreamsRepo` is provided by an implicit conversion from `Repo`, so that
 * Akka Streams can remain an optional dependency for longevity users.
 * otherwise, it would have been included as part of the [[RepoPool]].
 */
class AkkaStreamsRepo(repo: RepoPool) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToAkkaStream[P : TypeKey](query: Query[P]): Source[PState[P], NotUsed] =
    repo.pRepoMap[P].queryToAkkaStreamImpl(query)

}
