package longevity.persistence.inmem

import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemRepo.delete */
private[inmem] trait InMemDelete[P <: Persistent] {
  repo: InMemRepo[P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      repo.synchronized {
        assertNoWriteConflict(state)
        unregisterById(state)
        unregisterByKeyVals(state.orig)
      }
      new Deleted(state.get)
    }
  }

}
