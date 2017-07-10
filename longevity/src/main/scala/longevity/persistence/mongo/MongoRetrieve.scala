package longevity.persistence.mongo

import longevity.model.ptype.Key
import longevity.model.query.EqOp
import longevity.persistence.PState
import org.bson.conversions.Bson

/** implementation of MongoPRepo.retrieve */
private[mongo] trait MongoRetrieve[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def retrieve[V : Key[M, P, ?]](v: V): F[Option[PState[P]]] = {
    val fv = effect.pure(v)
    val fq = effect.map(fv) { v =>
      logger.debug(s"executing MongoPRepo.retrieve: $v")
      keyValQuery(v)
    }
    val fr = effect.mapBlocking(fq) { q =>
      mongoCollection.find(q).first
    }
    effect.map(fr) { r =>
      val so = Option(r).map(bsonToState)
      logger.debug(s"done executing MongoPRepo.retrieve: $so")
      so
    }
  }
 
  protected def keyValQuery[V : Key[M, P, ?]](keyVal: V): Bson = {
    val k = implicitly[Key[M, P, V]].keyValTypeKey
    mongoRelationalFilter[V](realizedPType.realizedKey(k).realizedProp.prop, EqOp, keyVal)
  }

}
