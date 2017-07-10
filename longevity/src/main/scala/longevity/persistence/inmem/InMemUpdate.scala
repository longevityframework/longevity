package longevity.persistence.inmem

import longevity.persistence.PState

/** implementation of InMemPRepo.update */
private[inmem] trait InMemUpdate[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def update(state: PState[P]) = {
    val fs = effect.pure(state)
    val fss = effect.map(fs) { state =>
      logger.debug(s"executing InMemPRepo.update: $state")
      validateStablePrimaryKey(state)
      val rowVersion = if (persistenceConfig.optimisticLocking) {
        state.rowVersion.map(_ + 1).orElse(Some(0L))
      } else {
        None
      }
      val newState = PState[P](state.id, rowVersion, None, None, state.get)
      (state, newState)
    }
    val fns = effect.mapBlocking(fss) { case (state, newState) =>
      repo.synchronized {
        assertNoWriteConflict(state)
        assertUniqueKeyVals(state)
        unregisterByKeyVals(state.orig)
        registerById(newState)
        registerByKeyVals(newState)
      }
      newState
    }
    effect.map(fns) { ns =>
      logger.debug(s"done executing InMemPRepo.update: $ns")
      ns
    }
  }

}
