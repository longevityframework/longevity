package longevity.persistence.mongo

import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.commons.MongoDBObject
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.create */
private[mongo] trait MongoUpdate[P <: Persistent] {
  repo: MongoRepo[P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    val p = state.get
    val objectId = state.id.asInstanceOf[MongoId[P]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = casbahForP(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = blocking {
      try {
        mongoCollection.update(query, casbah)
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    new PState[P](state.id, p)
  }

}
