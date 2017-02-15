package longevity.persistence.inmem

import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of InMemRepo.create */
private[inmem] trait InMemCreate[P] {
  repo: InMemRepo[P] =>

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling InMemRepo.create: $unpersisted")
      repo.synchronized {
        val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
        val state = PState(IntId[P](nextId), rowVersion, None, None, unpersisted)
        assertUniqueKeyVals(state)
        registerById(state)
        registerByKeyVals(state)
        logger.debug(s"done calling InMemRepo.create: $state")
        state
      }
    }
  }

}
