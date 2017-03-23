package longevity

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.Monad
import emblem.TypeKey
import emblem.typeKey
import fs2.Stream
import fs2.Task
import io.iteratee.{ Enumerator => CatsEnumerator }
import longevity.model.query.Query
import play.api.libs.iteratee.{ Enumerator => PlayEnumerator }
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** manages entity persistence operations */
package object persistence {

  /** provides repository methods that use Akka Streams for repository streaming
   * API.
   *
   * `AkkaStreamsRepo` is provided by an implicit conversion from `Repo`, so that
   * Akka Streams can remain an optional dependency for longevity users.
   * otherwise, it would have been included as part of the [[Repo]].
   */
  implicit class AkkaStreamsRepo[P](repo: Repo[P]) {

    /** streams persistent objects matching a query
     *
     * @param query the query to execute
     */
    def queryToAkkaStream(query: Query[P]): Source[PState[P], NotUsed] =
      repo.asInstanceOf[BaseRepo[P]].queryToAkkaStreamImpl(query)

  }

  /** provides repository methods that use FS2 for repository streaming API.
   *
   * `FS2Repo` is provided by an implicit conversion from `Repo`, so that FS2 can remain an optional
   * dependency for longevity users. otherwise, it would have been included as part of the [[Repo]].
   */
  implicit class FS2Repo[P](repo: Repo[P]) {

    /** streams persistent objects matching a query
     *
     * @param query the query to execute
     */
    def queryToFS2(query: Query[P]): Stream[Task, PState[P]] =
      repo.asInstanceOf[BaseRepo[P]].queryToFS2Impl(query)

  }

  /** provides repository methods that use iteratee.io for repository streaming API.
   *
   * `IterateeIoRepo` is provided by an implicit conversion from `Repo`, so that iteratee.io can
   * remain an optional dependency for longevity users. otherwise, it would have been included as
   * part of the [[Repo]].
   */
  implicit class IterateeIoRepo[P](repo: Repo[P]) {

    /** streams persistent objects matching a query
     *
     * @param query the query to execute
     */
    def queryToIterateeIo[F[_]](query: Query[P])(implicit F: Monad[F]): CatsEnumerator[F, PState[P]] =
      repo.asInstanceOf[BaseRepo[P]].queryToIterateeIoImpl[F](query)

  }

  /** provides repository methods that use Play iteratees for repository streaming API.
   *
   * `PlayRepo` is provided by an implicit conversion from `Repo`, so that Play iteratees can remain
   * an optional dependency for longevity users. otherwise, it would have been included as part of
   * the [[Repo]].
   */
  implicit class PlayRepo[P](repo: Repo[P]) {

    /** streams persistent objects matching a query
     *
     * @param query the query to execute
     */
    def queryToPlay[F[_]](query: Query[P])(implicit context: ExecutionContext): PlayEnumerator[PState[P]] =
      repo.asInstanceOf[BaseRepo[P]].queryToPlayImpl(query)

  }

  /** packages a persistent object with a `TypeKey` for the object's type. used
   * by [[RepoPool.createMany]].
   */
  implicit class PWithTypeKey[P : TypeKey](private[persistence] val p: P) {
    private[persistence] val pTypeKey = typeKey[P]
  }

  /** a future persistent state */
  type FPState[P] = Future[PState[P]]

  /** extension methods for an [[FPState]] */
  implicit class LiftFPState[P](
    fpState: FPState[P])(
    implicit executionContext: ExecutionContext) {

    /** map the future `PState` by mapping the `Persistent` inside */
    def mapP(f: P => P): FPState[P] =
      fpState.map { pState => pState.map { p => f(p) } }

    /** flatMap the future `PState` by flat-mapping the `Persistent` inside */
    def flatMapP(f: P => Future[P]): FPState[P] =
      fpState.flatMap { pState => f(pState.get) map { p => pState.set(p) } }

  }

  /** an optional persistent state */
  type OPState[P] = Option[PState[P]]

  /** extension methods for an [[OPState]] */
  implicit class LiftOPState[P](opState: OPState[P]) {

    /** map the optional `PState` by mapping the `Persistent` inside */
    def mapP(f: P => P): OPState[P] =
      opState.map { pState => pState.map { p => f(p) } }

    /** flatMap the optional `PState` by flat-mapping the `Persistent` inside */
    def flatMapP(f: P => Option[P]): OPState[P] =
      opState.flatMap { pState => f(pState.get) map { p => pState.set(p) } }

  }

  /** a future option persistent state */
  type FOPState[P] = Future[Option[PState[P]]]

  /** extension methods for an [[FOPState]] */
  implicit class LiftFOPState[P](
    fopState: FOPState[P])(
    implicit executionContext: ExecutionContext) {

    /** map the `FOPState` by mapping the `Persistent` inside the `PState` */
    def mapP(f: P => P): FOPState[P] =
      fopState.map { opState =>
        opState.map { pState => pState.map { p => f(p) } }
      }

    /** flatMap the `FOPState` by flat-mapping the `Persistent` inside */
    def flatMapP(f: P => Future[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState.get) map { p => Some(pState.set(p)) }
          case None => Future.successful(None)
        }
      }

    /** map the `FOPState` by mapping the `PState` inside */
    def mapState(f: PState[P] => PState[P]): FOPState[P] =
      fopState.map { opState => opState.map(f(_)) }

    /** flatMap the `FOPState` by flat-mapping the `PState` inside */
    def flatMapState(f: PState[P] => FPState[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState).map(Some(_))
          case None => Future.successful(None)
        }
      }

  }

}
