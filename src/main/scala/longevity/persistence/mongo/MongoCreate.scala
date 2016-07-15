package longevity.persistence.mongo

import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.commons.MongoDBObject
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoCreate[P <: Persistent] {
  repo: MongoRepo[P] =>

  def create(p: P)(implicit context: ExecutionContext) = Future {
    val objectId = new ObjectId()
    val casbah = casbahForP(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = blocking {
      try {
        mongoCollection.insert(casbah)
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    new PState[P](MongoId(objectId), p)
  }

}
