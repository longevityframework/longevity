package longevity.persistence.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.typesafe.scalalogging.LazyLogging
import emblem.stringUtil.typeName
import emblem.stringUtil.uncapitalize
import longevity.context.MongoConfig
import longevity.context.PersistenceConfig
import longevity.persistence.BaseRepo
import longevity.subdomain.DerivedPType
import longevity.subdomain.PType
import longevity.subdomain.Persistent
import longevity.subdomain.PolyPType
import longevity.subdomain.Subdomain
import org.bson.BsonDocument
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a MongoDB repository for persistent entities of type `P`.
 *
 * @param pType the persistent type of the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 * @param mongoDb the connection to the mongo database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class MongoRepo[P <: Persistent] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain,
  protected val session: MongoRepo.MongoSessionInfo,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, subdomain)
with MongoCreate[P]
with MongoDelete[P]
with MongoQuery[P]
with MongoRead[P]
with MongoRetrieve[P]
with MongoSchema[P]
with MongoUpdate[P]
with MongoWrite[P]
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

  case class MongoSessionInfo(config: MongoConfig) {
    lazy val client = new MongoClient(new MongoClientURI(config.uri))
    lazy val db = client.getDatabase(config.db)
  }

  def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    session: MongoSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[MongoRepo[_ >: P <: Persistent]])
  : MongoRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new MongoRepo(pType, subdomain, session, config) with PolyMongoRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: MongoRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: MongoRepo[Poly] = poly
          }
          with MongoRepo(pType, subdomain, session, config) with DerivedMongoRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new MongoRepo(pType, subdomain, session, config)
    }
    repo
  }

}
