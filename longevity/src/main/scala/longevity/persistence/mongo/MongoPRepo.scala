package longevity.persistence.mongo

import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.emblem.stringUtil.typeName
import longevity.emblem.stringUtil.uncapitalize
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.PRepo
import org.bson.BsonDocument

/** a MongoDB repository for persistent entities of type `P`.
 *
 * @param pType the persistent type of the entities this repository handles
 * @param modelType the model type containing the entities that this repo persists
 * @param mongoDb the connection to the mongo database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class MongoPRepo[M, P] private[persistence] (
  pType: PType[M, P],
  modelType: ModelType[M],
  protected val persistenceConfig: PersistenceConfig,
  protected val session: () => MongoSession)
extends PRepo[M, P](pType, modelType)
with MongoCreate[M, P]
with MongoDelete[M, P]
with MongoQuery[M, P]
with MongoRead[M, P]
with MongoRetrieve[M, P]
with MongoSchema[M, P]
with MongoUpdate[M, P]
with MongoWrite[M, P]
with LazyLogging {
  repo =>

  protected def collectionName = uncapitalize(typeName(pTypeKey.tpe))

  protected[mongo] def mongoCollection = session().db.getCollection(collectionName, classOf[BsonDocument])

  override def toString = s"MongoPRepo[${pTypeKey.name}]"

}

private[persistence] object MongoPRepo {

  def apply[M, P](
    pType: PType[M, P],
    modelType: ModelType[M],
    config: PersistenceConfig,
    polyRepoOpt: Option[MongoPRepo[M, _ >: P]],
    session: () => MongoSession)
  : MongoPRepo[M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new MongoPRepo(pType, modelType, config, session) with PolyMongoPRepo[M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: MongoPRepo[M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: MongoPRepo[M, Poly] = poly
          }
          with MongoPRepo(pType, modelType, config, session) with DerivedMongoPRepo[M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new MongoPRepo(pType, modelType, config, session)
    }
    repo
  }

}
