package longevity

import emblem.TypeKey
import emblem.typeKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import longevity.persistence.streams.AkkaStreamsRepo
import longevity.persistence.streams.FS2Repo
import longevity.persistence.streams.IterateeIoRepo
import longevity.persistence.streams.PlayRepo

/** manages entity persistence operations */
package object persistence {

  /** implicit conversion from [[RepoPool]] to [[longevity.persistence.streams.AkkaStreamsRepo
    * AkkaStreamsRepo]] */
  implicit def repoToAkkaStreamsRepo(repo: RepoPool) = new AkkaStreamsRepo(repo)

  /** implicit conversion from [[RepoPool]] to [[longevity.persistence.streams.FS2Repo FS2Repo]] */
  implicit def repoToFS2Repo(repo: RepoPool) = new FS2Repo(repo)

  /** implicit conversion from [[RepoPool]] to [[longevity.persistence.streams.IterateeIoRepo IterateeIoRepo]] */
  implicit def repoToIterateeIoRepo(repo: RepoPool) = new IterateeIoRepo(repo)

  /** implicit conversion from [[RepoPool]] to [[longevity.persistence.streams.PlayRepo PlayRepo]] */
  implicit def repoToPlayRepo(repo: RepoPool) = new PlayRepo(repo)

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
