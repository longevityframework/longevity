package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoUpdate[M, P] {
  repo: MongoRepo[M, P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.update: $state")
      validateStablePrimaryKey(state)
      val query = writeQuery(state)
      val updatedState = state.update(
        persistenceConfig.optimisticLocking,
        persistenceConfig.writeTimestamps)
      val document = bsonForState(updatedState)
      logger.debug(s"calling MongoCollection.replaceOne: $query $document")
      val updateResult = try {
        mongoCollection.replaceOne(query, document)
      } catch {
        case e: MongoWriteException => throwDuplicateKeyValException(state.get, e)
      }
      if (persistenceConfig.optimisticLocking && updateResult.getModifiedCount == 0) {
        throw new WriteConflictException(state)
      }
      logger.debug(s"done calling MongoRepo.update: $updatedState")
      updatedState
    }
  }

}
