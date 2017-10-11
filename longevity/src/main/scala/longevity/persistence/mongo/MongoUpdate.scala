package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.effect.Effect.Syntax
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import org.bson.BsonDocument
import org.bson.BsonBoolean

/** implementation of MongoPRepo.create */
private[mongo] trait MongoUpdate[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def update(state: PState[P]) = {
    val fqsd = effect.pure(state).map { s =>
      logger.debug(s"executing MongoPRepo.update: $s")
      validateStablePrimaryKey(s)
      val query = writeQuery(s)
      val updatedState = s.update(persistenceConfig.optimisticLocking, persistenceConfig.writeTimestamps)
      val document = bsonForState(updatedState)
      logger.debug(s"calling MongoCollection.replaceOne: $query $document")
      (query, updatedState, document)
    }
    val fsr = fqsd.mapBlocking { case (q, s, d) =>
      val r = try {
        mongoCollection.replaceOne(q, d)
      } catch {
        case e: MongoWriteException => throwDuplicateKeyValException(s.get, e)
      }
      (s, r)
    }
    fsr.map { case (s, r) =>
      if (persistenceConfig.optimisticLocking && r.getModifiedCount == 0) {
        throw new WriteConflictException(s)
      }
      logger.debug(s"done executing MongoPRepo.update: $s")
      s
    }
  }

  protected[persistence] def updateMigrationStarted(state: PState[P]): F[Unit] = {
    effect.pure(state).map(writeQuery).mapBlocking { q =>
      mongoCollection.updateOne(q, setTrue("_migrationStarted"))
      ()
    }
  }

  protected[persistence] def updateMigrationComplete(state: PState[P]): F[Unit] = {
    effect.pure(state).map(writeQuery).mapBlocking { q =>
      mongoCollection.updateOne(q, setTrue("_migrationComplete"))
      ()
    }
  }

  private def setTrue(name: String) = new BsonDocument("$set", new BsonDocument(name, BsonBoolean.TRUE))

}
