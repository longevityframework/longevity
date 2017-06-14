package longevity.persistence.inmem

import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemPRepo.update */
private[inmem] trait InMemUpdate[M, P] {
  repo: InMemPRepo[M, P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
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

}
