package longevity.persistence.mongo

import longevity.model.KVEv
import longevity.model.query.EqOp
import org.bson.conversions.Bson
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.retrieve */
private[mongo] trait MongoRetrieve[M, P] {
  repo: MongoRepo[M, P] =>

  override def retrieve[V : KVEv[M, P, ?]](keyVal: V)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.retrieve: $keyVal")
    
      val query = keyValQuery(keyVal)
      val result = mongoCollection.find(query).first
      val resultOption = Option(result)
      val stateOption = resultOption.map(bsonToState)

      logger.debug(s"done calling MongoRepo.retrieve: $stateOption")
      stateOption
    }
  }
 
  protected def keyValQuery[V : KVEv[M, P, ?]](keyVal: V): Bson = {
    val k = implicitly[KVEv[M, P, V]].key
    mongoRelationalFilter[V](realizedPType.realizedKey(k).realizedProp.prop, EqOp, keyVal)
  }

}
