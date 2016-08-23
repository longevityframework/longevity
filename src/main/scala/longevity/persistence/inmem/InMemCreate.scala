package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemRepo.create */
private[inmem] trait InMemCreate[P <: Persistent] {
  repo: InMemRepo[P] =>

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    blocking {
      repo.synchronized {
        val state = PState(IntId[P](nextId), persistenceConfig.modifiedDate, unpersisted)
        assertUniqueKeyVals(state)
        registerById(state)
        registerByKeyVals(state)
        state
      }
    }
  }

}
