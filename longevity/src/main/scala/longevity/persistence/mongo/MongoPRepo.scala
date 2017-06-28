package longevity.persistence.mongo

import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.context.Effect
import longevity.emblem.stringUtil.typeName
import longevity.emblem.stringUtil.uncapitalize
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.PRepo
import org.bson.BsonDocument

/** a MongoDB repository for persistent entities of type `P` */
private[longevity] class MongoPRepo[F[_], M, P] private[persistence] (
  effect: Effect[F],
  modelType: ModelType[M],
  pType: PType[M, P],
  protected val persistenceConfig: PersistenceConfig,
  protected val session: () => MongoSession)
extends PRepo[F, M, P](effect, modelType, pType)
with MongoCreate[F, M, P]
with MongoDelete[F, M, P]
with MongoQuery[F, M, P]
with MongoRead[F, M, P]
with MongoRetrieve[F, M, P]
with MongoSchema[F, M, P]
with MongoUpdate[F, M, P]
with MongoWrite[F, M, P]
with LazyLogging {
  repo =>

  protected def collectionName = uncapitalize(typeName(pTypeKey.tpe))

  protected[mongo] def mongoCollection = session().db.getCollection(collectionName, classOf[BsonDocument])

  override def toString = s"MongoPRepo[${pTypeKey.name}]"

}

private[persistence] object MongoPRepo {

  def apply[F[_], M, P](
    effect: Effect[F],
    modelType: ModelType[M],
    pType: PType[M, P],
    config: PersistenceConfig,
    polyRepoOpt: Option[MongoPRepo[F, M, _ >: P]],
    session: () => MongoSession)
  : MongoPRepo[F, M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new MongoPRepo(effect, modelType, pType, config, session) with PolyMongoPRepo[F, M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: MongoPRepo[F, M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: MongoPRepo[F, M, Poly] = poly
          }
          with MongoPRepo(effect, modelType, pType, config, session) with DerivedMongoPRepo[F, M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new MongoPRepo(effect, modelType, pType, config, session)
    }
    repo
  }

}
