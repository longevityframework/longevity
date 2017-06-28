package longevity.persistence.inmem

import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of InMemPRepo.delete */
private[inmem] trait InMemDelete[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def delete(state: PState[P]) = effect.mapBlocking(effect.pure(state)) { state =>
    logger.debug(s"calling InMemPRepo.delete: $state")
    validateStablePrimaryKey(state)
    repo.synchronized {
      assertNoWriteConflict(state)
      unregisterById(state)
      unregisterByKeyVals(state.orig)
    }
    val deleted = new Deleted(state.get)
    logger.debug(s"done calling InMemPRepo.delete: $deleted")
    deleted
  }

}
