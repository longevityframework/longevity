package longevity.persistence.inmem

import longevity.persistence.PState

/** implementation of InMemPRepo.create */
private[inmem] trait InMemCreate[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def create(p: P): F[PState[P]] = effect.mapBlocking(effect.pure(p)) { p =>
    logger.debug(s"calling InMemPRepo.create: $p")
    repo.synchronized {
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val state = PState(IntId[P](nextId), rowVersion, None, None, p)
      assertUniqueKeyVals(state)
      registerById(state)
      registerByKeyVals(state)
      logger.debug(s"done calling InMemPRepo.create: $state")
      state
    }
  }

}
