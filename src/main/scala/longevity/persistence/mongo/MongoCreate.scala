package longevity.persistence.mongo

import com.mongodb.DuplicateKeyException
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
    val id = new ObjectId()
    val modifiedDate = persistenceConfig.modifiedDate
    val writeResult = blocking {
      try {
        mongoCollection.insert(casbahForP(p, id, modifiedDate))
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    PState(MongoId[P](id), modifiedDate, p)
  }

}
