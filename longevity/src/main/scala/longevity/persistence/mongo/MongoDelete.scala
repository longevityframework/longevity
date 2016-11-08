package longevity.persistence.mongo

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.delete */
private[mongo] trait MongoDelete[P <: Persistent] {
  repo: MongoRepo[P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.delete: $state")
      validateStablePartitionKey(state)
      val query = writeQuery(state)
      val deleteResult = mongoCollection.deleteOne(query)
      if (persistenceConfig.optimisticLocking && deleteResult.getDeletedCount == 0) {
        throw new WriteConflictException(state)
      }
      val deleted = new Deleted(state.get)
      logger.debug(s"done calling MongoRepo.delete: $deleted")
      deleted
    }
  }

}
