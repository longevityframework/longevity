package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState

/** implementation of MongoPRepo.create */
private[mongo] trait MongoUpdate[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def update(state: PState[P]) = effect.mapBlocking(effect.pure(state)) { state =>
    logger.debug(s"calling MongoPRepo.update: $state")
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
    logger.debug(s"done calling MongoPRepo.update: $updatedState")
    updatedState
  }

}
