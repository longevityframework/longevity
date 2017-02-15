package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.persistence.PState
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoCreate[P] {
  repo: MongoRepo[P] =>

  def create(p: P)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.create: $p")
      val id = if (hasPrimaryKey) None else Some(MongoId[P](new ObjectId()))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
      val state = PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
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
