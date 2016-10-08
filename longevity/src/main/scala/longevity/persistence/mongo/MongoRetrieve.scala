package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
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
    logger.debug(s"calling MongoRepo.retrieve: $keyVal")
    val query = keyValQuery(keyVal)
    val resultOption = blocking {
      mongoCollection.findOne(query)
    }
    val stateOption = resultOption.map(dbObjectToPState)
    logger.debug(s"done calling MongoRepo.retrieve: $stateOption")
    stateOption
  }

  protected def keyValQuery[V <: KeyVal[P, V] : TypeKey](keyVal: V): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    val realizedKey = realizedPType.realizedKey[V]
    realizedKey.realizedProp.realizedPropComponents.foreach { component =>
      builder += component.outerPropPath.inlinedPath -> component.innerPropPath.get(keyVal)
    }
    builder.result
  }

}
