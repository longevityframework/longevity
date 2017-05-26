package longevity

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import longevity.model.PEv
import longevity.persistence.streams.AkkaStreamsRepo
import longevity.persistence.streams.FS2Repo
import longevity.persistence.streams.IterateeIoRepo
import longevity.persistence.streams.PlayRepo

/** manages entity persistence operations */
package object persistence {

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.AkkaStreamsRepo
   * AkkaStreamsRepo]]
   * 
   * @tparam M the model
   */
  implicit def repoToAkkaStreamsRepo[M](repo: Repo[M]) = new AkkaStreamsRepo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.FS2Repo FS2Repo]]
   * 
   * @tparam M the model
   */
  implicit def repoToFS2Repo[M](repo: Repo[M]) = new FS2Repo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.IterateeIoRepo IterateeIoRepo]]
   * 
   * @tparam M the model
   */
  implicit def repoToIterateeIoRepo[M](repo: Repo[M]) = new IterateeIoRepo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.PlayRepo PlayRepo]]
   * 
   * @tparam M the model
   */
  implicit def repoToPlayRepo[M](repo: Repo[M]) = new PlayRepo(repo)

  /** packages a persistent object along with evidence for the persistent class. used by
   * [[Repo.createMany]].
   */
  implicit class PWithEv[M, P](
    private[persistence] val p: P)(
    private[persistence] val ev: PEv[M, P])

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
