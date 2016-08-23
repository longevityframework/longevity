package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.delete */
private[mongo] trait MongoDelete[P <: Persistent] {
  repo: MongoRepo[P] =>

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    val query = deleteQuery(state)
    val writeResult = blocking {
      mongoCollection.remove(query)
    }
    if (persistenceConfig.optimisticLocking && writeResult.getN == 0) {
      throw new WriteConflictException(state)
    }
    new Deleted(state.get)
  }

  protected def deleteQuery(state: PState[P]): MongoDBObject = {
    val builder = new MongoDBObjectBuilder()
    builder += "_id" -> mongoId(state)
    if (persistenceConfig.optimisticLocking) {
      builder += "_modifiedDate" -> state.modifiedDate
    }
    builder.result()
  }

}
