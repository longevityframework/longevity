package longevity.persistence.streams

import cats.Monad
import io.iteratee.Enumerator
import longevity.model.query.Query
import longevity.persistence.PRepo
import longevity.persistence.PState
import longevity.persistence.OldRepo

/** provides repository methods that use iteratee.io for repository streaming API.
 *
 * `IterateeIoRepo` is provided by an implicit conversion from `Repo`, so that iteratee.io can
 * remain an optional dependency for longevity users. otherwise, it would have been included as
 * part of the [[OldRepo]].
 */
class IterateeIoRepo[P](repo: OldRepo[P]) {

  /** streams persistent objects matching a query
   *
   * @param query the query to execute
   */
  def queryToIterateeIo[F[_]](query: Query[P])(implicit F: Monad[F]): Enumerator[F, PState[P]] =
    repo.asInstanceOf[PRepo[P]].queryToIterateeIoImpl[F](query)

}
