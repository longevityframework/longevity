package longevity.persistence.inmem

import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of InMemPRepo.delete */
private[inmem] trait InMemDelete[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def delete(state: PState[P]) = {
    val fs = effect.pure(state)
    val fs2 = effect.map(fs) { state =>
      logger.debug(s"executing InMemPRepo.delete: $state")
      validateStablePrimaryKey(state)
      state
    }
    val fs3 = effect.mapBlocking(fs2) { state =>
      repoSynchronized {
        assertNoWriteConflict(state)
        unregisterById(state)
        unregisterByKeyVals(state.orig)
      }
      state
    }
    effect.map(fs3) { state =>
      val deleted = new Deleted(state.get)
      logger.debug(s"done executing InMemPRepo.delete: $deleted")
      deleted
    }
  }

}
