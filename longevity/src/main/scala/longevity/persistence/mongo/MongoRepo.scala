package longevity.persistence.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.typesafe.scalalogging.LazyLogging
import emblem.stringUtil.typeName
import emblem.stringUtil.uncapitalize
import longevity.config.MongoDBConfig
import longevity.config.PersistenceConfig
import longevity.persistence.PRepo
import longevity.model.DerivedPType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.ModelType
import org.bson.BsonDocument
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a MongoDB repository for persistent entities of type `P`.
 *
 * @param pType the persistent type of the entities this repository handles
 * @param modelType the model type containing the entities that this repo persists
 * @param mongoDb the connection to the mongo database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class MongoRepo[M, P] private[persistence] (
  pType: PType[M, P],
  modelType: ModelType[M],
  protected val session: MongoRepo.MongoSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
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
  protected[mongo] lazy val mongoCollection = session.db.getCollection(collectionName, classOf[BsonDocument])

  protected[persistence] def close()(implicit executionContext: ExecutionContext) = Future {
    blocking { session.client.close() }
    ()
  }

  override def toString = s"MongoRepo[${pTypeKey.name}]"

}

private[persistence] object MongoRepo {

  case class MongoSessionInfo(config: MongoDBConfig) {
    lazy val client = new MongoClient(new MongoClientURI(config.uri))
    lazy val db = client.getDatabase(config.db)
  }

  def apply[M, P](
    pType: PType[M, P],
    modelType: ModelType[M],
    session: MongoSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[MongoRepo[M, _ >: P]])
  : MongoRepo[M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new MongoRepo(pType, modelType, session, config) with PolyMongoRepo[M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: MongoRepo[M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: MongoRepo[M, Poly] = poly
          }
          with MongoRepo(pType, modelType, session, config) with DerivedMongoRepo[M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new MongoRepo(pType, modelType, session, config)
    }
    repo
  }

}
