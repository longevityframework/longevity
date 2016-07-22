package longevity

import akka.NotUsed
import akka.stream.scaladsl.Source
import emblem.TypeKey
import emblem.typeKey
import longevity.subdomain.ptype.Query
import longevity.subdomain.persistent.Persistent
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

/** manages entity persistence operations */
package object persistence {

  /** provides repository methods that use Akka Streams for repository streaming
   * API.
   * 
   * `StreamingRepo` is provided by an implicit conversion from `Repo`, so that
   * Akka Streams can remain an optional dependency for longevity users.
   * otherwise, it would have been included as part of the [[Repo]].
   */
  implicit class StreamingRepo[P <: Persistent](repo: Repo[P]) {

    /** streams persistent objects matching a query
     * 
     * @param query the query to execute
     */
    def streamByQuery(query: Query[P]): Source[PState[P], NotUsed] =
      repo.asInstanceOf[BaseRepo[P]].streamByQueryImpl(query)

  }

  /** packages a [[longevity.subdomain.persistent.Persistent persistent object]]
   * with a `TypeKey` for the object's type. used by [[RepoPool.createMany]].
   */
  implicit class PWithTypeKey[P <: Persistent : TypeKey](val p: P) {
    val pTypeKey = typeKey[P]
  }

  /** a future persistent state */
  type FPState[P <: Persistent] = Future[PState[P]]

  /** extension methods for an [[FPState]] */
  implicit class LiftFPState[P <: Persistent](
    fpState: FPState[P])(
    implicit executionContext: ExecutionContext) {

    /** map the future PState by mapping the Persistent inside the PState */
    def mapP(f: P => P): FPState[P] =
      fpState.map { pState => pState.map { p => f(p) } }

    /** flatMap the future PState by mapping the Persistent inside the PState into a `Future[P]` */
    def flatMapP(f: P => Future[P]): FPState[P] =
      fpState.flatMap { pState => f(pState.get) map { p => pState.set(p) } }

  }

  /** a future option persistent state */
  type FOPState[P <: Persistent] = Future[Option[PState[P]]]

  /** extension methods for an [[FOPState]] */
  implicit class LiftFOPState[P <: Persistent](
    fopState: FOPState[P])(
    implicit executionContext: ExecutionContext) {

    /** map the `FOPState` by mapping the `Persistent` inside the `PState` */
    def mapP(f: P => P): FOPState[P] =
      fopState.map { opState =>
        opState.map { pState => pState.map { p => f(p) } }
      }

    /** flatMap the `FOPState` by mapping the `Persistent` inside the `PState` into a `Future[P]` */
    def flatMapP(f: P => Future[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState.get) map { p => Some(pState.set(p)) }
          case None => Future.successful(None)
        }
      }

    /** map the `FOPState` by mapping the `PState` inside the `Option` */
    def mapState(f: PState[P] => PState[P]): FOPState[P] =
      fopState.map { opState => opState.map(f(_)) }

    /** flatMap the `FOPState` by mapping the `PState` inside the `Option` into a `FPState[P]` */
    def flatMapState(f: PState[P] => FPState[P]): FOPState[P] =
      fopState.flatMap { opState =>
        opState match {
          case Some(pState) => f(pState).map(Some(_))
          case None => Future.successful(None)
        }
      }

  }

}
