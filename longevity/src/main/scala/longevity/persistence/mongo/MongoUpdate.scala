package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState

/** implementation of MongoPRepo.create */
private[mongo] trait MongoUpdate[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def update(state: PState[P]) = {
    val fs = effect.pure(state)
    val fqsd = effect.map(fs) { s =>
      logger.debug(s"executing MongoPRepo.update: $s")
      validateStablePrimaryKey(s)
      val query = writeQuery(s)
      val updatedState = s.update(persistenceConfig.optimisticLocking, persistenceConfig.writeTimestamps)
      val document = bsonForState(updatedState)
      logger.debug(s"calling MongoCollection.replaceOne: $query $document")
      (query, updatedState, document)
    }
    val fsr = effect.mapBlocking(fqsd) { case (q, s, d) =>
      val r = try {
        mongoCollection.replaceOne(q, d)
      } catch {
        case e: MongoWriteException => throwDuplicateKeyValException(s.get, e)
      }
      (s, r)
    }
    effect.map(fsr) { case (s, r) =>
      if (persistenceConfig.optimisticLocking && r.getModifiedCount == 0) {
        throw new WriteConflictException(s)
      }
      logger.debug(s"done executing MongoPRepo.update: $s")
      s
    }
  }

}
