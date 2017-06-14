package longevity.persistence.mongo

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoPRepo.delete */
private[mongo] trait MongoDelete[M, P] {
  repo: MongoPRepo[M, P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoPRepo.delete: $state")
      validateStablePrimaryKey(state)
      val query = writeQuery(state)
      val deleteResult = mongoCollection.deleteOne(query)
      if (persistenceConfig.optimisticLocking && deleteResult.getDeletedCount == 0) {
        throw new WriteConflictException(state)
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling MongoPRepo.delete: $deleted")
      deleted
    }
  }

}
