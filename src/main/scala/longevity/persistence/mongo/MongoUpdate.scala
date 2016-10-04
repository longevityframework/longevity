package longevity.persistence.mongo

import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import longevity.exceptions.persistence.WriteConflictException
import longevity.persistence.PState
import longevity.subdomain.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoUpdate[P <: Persistent] {
  repo: MongoRepo[P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    logger.debug(s"calling MongoRepo.update: $state")
    val query = buildQuery(state)
    val rowVersion = if (persistenceConfig.optimisticLocking) {
      state.rowVersion.map(_ + 1).orElse(Some(0L))
    } else {
      None
    }
    val casbah = casbahForP(state.get, mongoId(state), rowVersion)
    logger.debug(s"calling MongoCollection.update: $casbah")
    val writeResult = try {
      blocking {
        mongoCollection.update(query, casbah)
      }
    } catch {
      case e: DuplicateKeyException => throwDuplicateKeyValException(state.get, e)
    }
    if (persistenceConfig.optimisticLocking && writeResult.getN == 0) {
      throw new WriteConflictException(state)
    }
    val newState = PState[P](state.id, rowVersion, state.get)
    logger.debug(s"done calling MongoRepo.update: $newState")
    newState
  }

  private def buildQuery(state: PState[P]) = {
    val builder = new MongoDBObjectBuilder()
    builder += "_id" -> mongoId(state)
    if (persistenceConfig.optimisticLocking) {
      builder += "_rowVersion" -> state.rowVersion
    }
    builder.result()
  }

}
