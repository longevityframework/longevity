package longevity.persistence.mongo

import com.mongodb.client.model.Filters
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.Persistent
import org.bson.BsonInt64
import org.bson.BsonObjectId
import org.bson.conversions.Bson
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.delete */
private[mongo] trait MongoDelete[P <: Persistent] {
  repo: MongoRepo[P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    logger.debug(s"calling MongoRepo.delete: $state")
    val query = deleteQuery(state)
    val deleteResult = blocking {
      mongoCollection.deleteOne(query)
    }
    if (persistenceConfig.optimisticLocking && deleteResult.getDeletedCount == 0) {
      throw new WriteConflictException(state)
    }
    val deleted = new Deleted(state.get)
    logger.debug(s"done calling MongoRepo.delete: $deleted")
    deleted
  }

  protected def deleteQuery(state: PState[P]): Bson = {
    val idFilter = Filters.eq("_id", new BsonObjectId(mongoId(state)))
    val filter = if (persistenceConfig.optimisticLocking) {
      val rvFilter = state.rowVersion match {
        case Some(rv) => Filters.eq("_rowVersion", new BsonInt64(rv))
        case None => Filters.not(Filters.exists("_rowVersion"))
      }
      Filters.and(idFilter, rvFilter)
    } else {
      idFilter
    }
    filter
  }

}
