package longevity.persistence.inmem

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
      repo.synchronized {
        val state = PState(IntId[P](nextId), r, None, None, p)
        assertUniqueKeyVals(state)
        registerById(state)
        registerByKeyVals(state)
        state
      }
    }
    effect.map(fs) { s =>
      logger.debug(s"done executing InMemPRepo.create: $s")
      s
    }
  }

}
