package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.update */
private[inmem] trait InMemUpdate[P <: Persistent] {
  repo: InMemRepo[P] =>

  // TODO: consider putting `blocking` around each of the repo.synchronized

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
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
