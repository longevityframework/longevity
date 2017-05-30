package longevity.persistence.streams

import cats.Monad
import io.iteratee.Enumerator
import longevity.model.PEv
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.persistence.Repo

/** provides repository methods that use iteratee.io for repository streaming API.
 *
 * `IterateeIoRepo` is provided by an implicit conversion from `Repo`, so that iteratee.io can
 * remain an optional dependency for longevity users. otherwise, it would have been included as
 * part of the [[Repo]].
 * 
 * @tparam M the model
 */
class IterateeIoRepo[M](repo: Repo[M]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToIterateeIo[P: PEv[M, ?], F[_]](query: Query[P])(implicit F: Monad[F]): Enumerator[F, PState[P]] =
    repo.pRepoMap(implicitly[PEv[M, P]].key).queryToIterateeIoImpl[F](query)

}
