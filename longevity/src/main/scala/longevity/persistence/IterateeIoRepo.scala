package longevity.persistence

import cats.Monad
import io.iteratee.Enumerator
import longevity.model.query.Query

/** provides repository methods that use iteratee.io for repository streaming API.
 *
 * `IterateeIoRepo` is provided by an implicit conversion from `Repo`, so that iteratee.io can
 * remain an optional dependency for longevity users. otherwise, it would have been included as
 * part of the [[Repo]].
 */
class IterateeIoRepo[P](repo: Repo[P]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToIterateeIo[F[_]](query: Query[P])(implicit F: Monad[F]): Enumerator[F, PState[P]] =
    repo.asInstanceOf[BaseRepo[P]].queryToIterateeIoImpl[F](query)

}
