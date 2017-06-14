package longevity.persistence.mongo

import longevity.model.ptype.Key
import longevity.model.query.EqOp
import org.bson.conversions.Bson
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoPRepo.retrieve */
private[mongo] trait MongoRetrieve[M, P] {
  repo: MongoPRepo[M, P] =>

  override def retrieve[V : Key[M, P, ?]](keyVal: V)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoPRepo.retrieve: $keyVal")
    
      val query = keyValQuery(keyVal)
      val result = mongoCollection.find(query).first
      val resultOption = Option(result)
      val stateOption = resultOption.map(bsonToState)

      logger.debug(s"done calling MongoPRepo.retrieve: $stateOption")
      stateOption
    }
  }
 
  protected def keyValQuery[V : Key[M, P, ?]](keyVal: V): Bson = {
    val k = implicitly[Key[M, P, V]].keyValTypeKey
    mongoRelationalFilter[V](realizedPType.realizedKey(k).realizedProp.prop, EqOp, keyVal)
  }

}
