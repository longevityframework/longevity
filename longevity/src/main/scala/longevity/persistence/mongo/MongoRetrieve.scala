package longevity.persistence.mongo

import emblem.TypeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.query.EqOp
import org.bson.conversions.Bson
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.retrieve */
private[mongo] trait MongoRetrieve[P] {
  repo: MongoRepo[P] =>

  override def retrieve[V <: KeyVal[P] : TypeKey](keyVal: V)(implicit context: ExecutionContext) = Future {
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
 
  protected def keyValQuery[V <: KeyVal[P] : TypeKey](keyVal: V): Bson = {
    mongoRelationalFilter[V](realizedPType.realizedKey[V].realizedProp.prop, EqOp, keyVal)
  }

}
