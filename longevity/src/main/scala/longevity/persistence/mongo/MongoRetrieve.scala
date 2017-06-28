package longevity.persistence.mongo

import longevity.model.ptype.Key
import longevity.model.query.EqOp
import org.bson.conversions.Bson

/** implementation of MongoPRepo.retrieve */
private[mongo] trait MongoRetrieve[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def retrieve[V : Key[M, P, ?]](v: V) = effect.mapBlocking(effect.pure(v)) { v =>
    logger.debug(s"calling MongoPRepo.retrieve: $v")    
    val query = keyValQuery(v)
    val result = mongoCollection.find(query).first
    val resultOption = Option(result)
    val stateOption = resultOption.map(bsonToState)
    logger.debug(s"done calling MongoPRepo.retrieve: $stateOption")
    stateOption
  }
 
  protected def keyValQuery[V : Key[M, P, ?]](keyVal: V): Bson = {
    val k = implicitly[Key[M, P, V]].keyValTypeKey
    mongoRelationalFilter[V](realizedPType.realizedKey(k).realizedProp.prop, EqOp, keyVal)
  }

}
