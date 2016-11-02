package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.persistence.PState
import longevity.subdomain.Persistent
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoCreate[P <: Persistent] {
  repo: MongoRepo[P] =>

  def create(p: P)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.create: $p")
      val id = if (hasPartitionKey) None else Some(MongoId[P](new ObjectId()))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val state = PState(id, rowVersion, p)
      val document = bsonForState(state)
      logger.debug(s"calling MongoCollection.insertOne: $document")
      try {
        mongoCollection.insertOne(document)
      } catch {
        case e: MongoWriteException => throwDuplicateKeyValException(p, e)
      }
      logger.debug(s"done calling MongoRepo.create: $state")
      state
    }
  }

}
