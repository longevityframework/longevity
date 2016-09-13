package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemRepo.update */
private[inmem] trait InMemUpdate[P <: Persistent] {
  repo: InMemRepo[P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      repo.synchronized {
        logger.debug(s"calling InMemRepo.update: $state")
        assertNoWriteConflict(state)
        assertUniqueKeyVals(state)
        unregisterByKeyVals(state.orig)
        val rowVersion = if (persistenceConfig.optimisticLocking) {
          state.rowVersion.map(_ + 1).orElse(Some(0L))
        } else {
          None
        }
        val newState = PState[P](state.id, rowVersion, state.get)
        registerById(newState)
        registerByKeyVals(newState)
        logger.debug(s"done calling InMemRepo.update: $newState")
        newState
      }
    }
  }

}
