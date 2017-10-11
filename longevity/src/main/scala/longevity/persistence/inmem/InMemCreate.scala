package longevity.persistence.inmem

import longevity.effect.Effect.Syntax
import longevity.persistence.PState

/** implementation of InMemPRepo.create */
private[inmem] trait InMemCreate[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def create(p: P): F[PState[P]] = {
    val fp = effect.pure(p)
    val fpr = effect.map(fp) { p =>
      logger.debug(s"executing InMemPRepo.create: $p")
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      (p, rowVersion)
    }
    val fs = effect.mapBlocking(fpr) { case (p, r) =>
      repo.synchronized(createStateBlocking(PState(IntId(nextId), r, None, None, p)))
    }
    effect.map(fs) { s =>
      logger.debug(s"done executing InMemPRepo.create: $s")
      s
    }
  }

  private[persistence] def createState(state: PState[P]): F[PState[P]] =
    effect.pure(()).map(_ => repo.synchronized(createStateBlocking(state)))

  // calls to this method must be wrapped in a repo.synchronized call
  private def createStateBlocking(state: PState[P]): PState[P] = {
    assertUniqueKeyVals(state)
    registerById(state)
    registerByKeyVals(state)
    state
  }

}
