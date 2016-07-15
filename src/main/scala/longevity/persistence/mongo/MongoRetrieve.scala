package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.retrieve */
private[mongo] trait MongoRetrieve[P <: Persistent] {
  repo: MongoRepo[P] =>

  override def retrieve[V <: KeyVal[P, V]](keyVal: V)(implicit context: ExecutionContext) = Future {
    val query = keyValQuery(keyVal)
    val resultOption = blocking {
      mongoCollection.findOne(query)
    }
    resultOption.map(dbObjectToPState)
  }

  protected def keyValQuery[V <: KeyVal[P, V]](keyVal: V): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    val realizedKey = realizedPType.realizedKeys(keyVal.key)
    realizedKey.realizedProp.basicPropComponents.foreach { basicPropComponent =>
      builder += basicPropComponent.outerPropPath.inlinedPath -> basicPropComponent.innerPropPath.get(keyVal)
    }
    builder.result
  }

}
