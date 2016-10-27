package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import com.mongodb.client.model.Filters
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import longevity.subdomain.Persistent
import org.bson.BsonInt64
import org.bson.BsonObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoUpdate[P <: Persistent] {
  repo: MongoRepo[P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.update: $state")
      val query = updateQuery(state)
      val updatedState = state.update(persistenceConfig.optimisticLocking)
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

  private def updateQuery(state: PState[P]) = {
    val idBson = Filters.eq("_id", new BsonObjectId(mongoId(state)))
    if (persistenceConfig.optimisticLocking) {
      val rvBson = state.rowVersion match {
        case Some(rv) => Filters.eq("_rowVersion", new BsonInt64(rv))
        case None => Filters.exists("_rowVersion", false)
      }
      Filters.and(idBson, rvBson)
    } else {
      idBson
    }
  }

}
