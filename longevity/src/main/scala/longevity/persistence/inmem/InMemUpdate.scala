package longevity.persistence.inmem

import longevity.persistence.PState

/** implementation of InMemPRepo.update */
private[inmem] trait InMemUpdate[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def update(state: PState[P]) = effect.mapBlocking(effect.pure(state)) { state =>
    repo.synchronized {
      logger.debug(s"calling InMemPRepo.update: $state")
      validateStablePrimaryKey(state)
      assertNoWriteConflict(state)
      assertUniqueKeyVals(state)
      unregisterByKeyVals(state.orig)
      val rowVersion = if (persistenceConfig.optimisticLocking) {
        state.rowVersion.map(_ + 1).orElse(Some(0L))
      } else {
        None
      }
      val newState = PState[P](state.id, rowVersion, None, None, state.get)
      registerById(newState)
      registerByKeyVals(newState)
      logger.debug(s"done calling InMemPRepo.update: $newState")
      newState
    }
  }

}
