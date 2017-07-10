package longevity.persistence.mongo

import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState

/** implementation of MongoPRepo.delete */
private[mongo] trait MongoDelete[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def delete(state: PState[P]) = {
    val fs = effect.pure(state)
    val fsq = effect.map(fs) { s =>
      logger.debug(s"executing MongoPRepo.delete: $s")
      validateStablePrimaryKey(s)
      (s, writeQuery(s))
    }
    val fsr = effect.mapBlocking(fsq) { case (s, q) =>
      (s, mongoCollection.deleteOne(q))
    }
    effect.map(fsr) { case (s, r) =>
      if (persistenceConfig.optimisticLocking && r.getDeletedCount == 0) {
        throw new WriteConflictException(s)
      }
      val d = new Deleted(s.get)
      logger.debug(s"done executing MongoPRepo.delete: $d")
      d
    }
  }

}
