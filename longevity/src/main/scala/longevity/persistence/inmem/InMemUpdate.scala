package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.effect.Effect.Syntax

/** implementation of InMemPRepo.update */
private[inmem] trait InMemUpdate[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def update(state: PState[P]) = {
    val fs = effect.pure(state)
    val fss = fs.map { state =>
      logger.debug(s"executing InMemPRepo.update: $state")
      validateStablePrimaryKey(state)
      val rowVersion = if (persistenceConfig.optimisticLocking) {
        state.rowVersion.map(_ + 1).orElse(Some(0L))
      } else {
        None
      }
      val newState = state.copy(rowVersion = rowVersion, orig = state.get)
      (state, newState)
    }
    val fns = fss.mapBlocking { case (state, newState) =>
      repoSynchronized {
        assertNoWriteConflict(state)
        assertUniqueKeyVals(state)
        unregisterByKeyVals(state.orig)
        registerById(newState)
        registerByKeyVals(newState)
      }
      newState
    }
    fns.map { ns =>
      logger.debug(s"done executing InMemPRepo.update: $ns")
      ns
    }
  }

  // these implementations are a bit naive, but should be sufficient for inmem migrations:

  protected[persistence] def updateMigrationStarted(state: PState[P]): F[Unit] =
    update(state.copy(migrationStarted = true)).map(_ => ())

  protected[persistence] def updateMigrationComplete(state: PState[P]): F[Unit] =
    update(state.copy(migrationComplete = true)).map(_ => ())

}
