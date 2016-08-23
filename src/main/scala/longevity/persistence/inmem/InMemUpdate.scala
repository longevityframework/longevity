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
        assertNoWriteConflict(state)
        assertUniqueKeyVals(state)
        unregisterByKeyVals(state.orig)
        val newState = state.copy(orig = state.get, modifiedDate = persistenceConfig.modifiedDate)
        registerById(newState)
        registerByKeyVals(newState)
        newState
      }
    }
  }

}
