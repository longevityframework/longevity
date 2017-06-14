package longevity.persistence.inmem

import longevity.persistence.Deleted
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemPRepo.delete */
private[inmem] trait InMemDelete[M, P] {
  repo: InMemPRepo[M, P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
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

}
