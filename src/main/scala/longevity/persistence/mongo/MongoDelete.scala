package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
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
    new Deleted(state.get)
  }

  protected def deleteQuery(state: PState[P]): MongoDBObject = {
    val objectId = state.id.asInstanceOf[MongoId[P]].objectId
    MongoDBObject("_id" -> objectId)
  }

}
