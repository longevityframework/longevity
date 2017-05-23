package longevity.persistence.inmem

import longevity.persistence.Deleted
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemRepo.delete */
private[inmem] trait InMemDelete[M, P] {
  repo: InMemRepo[M, P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling InMemRepo.delete: $state")
      validateStablePrimaryKey(state)
      repo.synchronized {
        assertNoWriteConflict(state)
        unregisterById(state)
        unregisterByKeyVals(state.orig)
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling InMemRepo.delete: $deleted")
      deleted
    }
  }

}
