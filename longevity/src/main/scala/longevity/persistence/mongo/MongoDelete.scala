package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.MongoDBObjectBuilder
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
    logger.debug(s"calling MongoRepo.delete: $state")
    val query = deleteQuery(state)
    val writeResult = blocking {
      mongoCollection.remove(query)
    }
    if (persistenceConfig.optimisticLocking && writeResult.getN == 0) {
      throw new WriteConflictException(state)
    }
    val deleted = new Deleted(state.get)
    logger.debug(s"done calling MongoRepo.delete: $deleted")
    deleted
  }

  protected def deleteQuery(state: PState[P]): MongoDBObject = {
    val builder = new MongoDBObjectBuilder()
    builder += "_id" -> mongoId(state)
    if (persistenceConfig.optimisticLocking) {
      builder += "_rowVersion" -> state.rowVersion
    }
    builder.result()
  }

}
