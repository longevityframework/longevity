package longevity.persistence.mongo

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of MongoPRepo.delete */
private[mongo] trait MongoDelete[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def delete(state: PState[P]) = effect.mapBlocking(effect.pure(state)) { state =>
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
