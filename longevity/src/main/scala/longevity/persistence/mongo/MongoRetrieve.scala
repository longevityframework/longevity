package longevity.persistence.mongo

import org.bson.BsonDocument
import emblem.TypeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.retrieve */
private[mongo] trait MongoRetrieve[P <: Persistent] {
  repo: MongoRepo[P] =>

  override def retrieve[V <: KeyVal[P, V] : TypeKey](keyVal: V)(implicit context: ExecutionContext) = Future {
    blocking {
      logger.debug(s"calling MongoRepo.retrieve: $keyVal")
    
      val query = keyValQuery(keyVal)
      val result = mongoCollection.find(query).first
      val resultOption = Option(result)
      val stateOption = resultOption.map(dbObjectToPState)

      logger.debug(s"done calling MongoRepo.retrieve: $stateOption")
      stateOption
    }
  }
 
  protected def keyValQuery[V <: KeyVal[P, V] : TypeKey](keyVal: V): BsonDocument = {
    val document = new BsonDocument
    val keyPath = realizedPType.realizedKey[V].realizedProp.inlinedPath
    val keyValBson = subdomainToBsonTranslator.translate(keyVal, false)
    document.append(keyPath, keyValBson)
    document
  }

}
