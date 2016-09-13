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
    logger.debug(s"calling MongoRepo.create: $p")
    val id = new ObjectId()
    val modifiedDate = persistenceConfig.modifiedDate
    val casbah = casbahForP(p, id, modifiedDate)
    logger.debug(s"calling MongoCollection.insert: $casbah")
    val writeResult = blocking {
      try {
        mongoCollection.insert(casbah)
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    val state = PState(MongoId[P](id), modifiedDate, p)
    logger.debug(s"done calling MongoRepo.create: $state")
    state
  }

}
