package longevity.persistence.mongo

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.mongodb.DBObject
import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoCursor
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.Config
import emblem.TypeKey
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.BaseRepo
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.PersistedAssoc
import longevity.subdomain.Assoc
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.ptype.Prop
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.Query.All
import longevity.subdomain.ptype.Query.AndOp
import longevity.subdomain.ptype.Query.EqOp
import longevity.subdomain.ptype.Query.GtOp
import longevity.subdomain.ptype.Query.GteOp
import longevity.subdomain.ptype.Query.LtOp
import longevity.subdomain.ptype.Query.LteOp
import longevity.subdomain.ptype.Query.NeqOp
import longevity.subdomain.ptype.Query.OrOp
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a MongoDB repository for persistent entities of type `P`.
 *
 * @param pType the persistent type of the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 * @param mongoDb the connection to the mongo database
 */
private[longevity] class MongoRepo[P <: Persistent] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain,
  mongoDb: MongoDB)
extends BaseRepo[P](pType, subdomain)
with MongoSchema[P] {
  repo =>

  private val collectionName = camelToUnderscore(typeName(pTypeKey.tpe))
  protected[mongo] val mongoCollection = mongoDb(collectionName)
  private val shorthandPool = subdomain.shorthandPool

  protected lazy val persistentToCasbahTranslator =
    new PersistentToCasbahTranslator(subdomain.emblematic, repoPool)

  private lazy val casbahToPersistentTranslator =
    new CasbahToPersistentTranslator(subdomain.emblematic, repoPool)

  def create(p: P)(implicit context: ExecutionContext) = Future {
    val objectId = new ObjectId()
    val casbah = casbahForP(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = blocking {
      try {
        mongoCollection.insert(casbah)
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    new PState[P](MongoId(objectId), p)
  }

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] = Future {
    blocking {
      queryCursor(query).toSeq.map(dbObjectToPState)
    }
  }

  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed] = 
    Source.fromIterator { () => queryCursor(query).map(dbObjectToPState) }

  private def queryCursor(query: Query[P]): MongoCursor = {
    mongoCollection.find(mongoQuery(query))
  }

  private def dbObjectToPState(dbObject: DBObject): PState[P] = {
    val id = dbObject.getAs[ObjectId]("_id").get
    val p = casbahToPersistentTranslator.translate(dbObject)(pTypeKey)
    new PState[P](MongoId(id), p)
  }

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    val p = state.get
    val objectId = state.assoc.asInstanceOf[MongoId[P]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = casbahForP(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = blocking {
      try {
        mongoCollection.update(query, casbah)
      } catch {
        case e: DuplicateKeyException => throwDuplicateKeyValException(p, e)
      }
    }
    new PState[P](state.passoc, p)
  }

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    val query = deleteQuery(state)
    val writeResult = blocking {
      mongoCollection.remove(query)
    }
    new Deleted(state.get, state.assoc)
  }

  protected def deleteQuery(state: PState[P]): MongoDBObject = {
    val objectId = state.assoc.asInstanceOf[MongoId[P]].objectId
    MongoDBObject("_id" -> objectId)
  }

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = Future {
    val query = persistedAssocQuery(assoc)
    val resultOption = blocking {
      mongoCollection.findOne(query)
    }
    val pOption = resultOption map { casbahToPersistentTranslator.translate(_)(pTypeKey) }
    pOption map { p => new PState[P](assoc, p) }
  }

  protected def persistedAssocQuery(assoc: PersistedAssoc[P]): MongoDBObject = {
    val objectId = assoc.asInstanceOf[MongoId[P]].objectId
    MongoDBObject("_id" -> objectId)
  }

  override protected def retrieveByKeyVal(keyVal: KeyVal[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = Future {
    val query = keyValQuery(keyVal)
    val resultOption = blocking {
      mongoCollection.findOne(query)
    }
    val idPOption = resultOption map { result =>
      val id = result.getAs[ObjectId]("_id").get
      id -> casbahToPersistentTranslator.translate(result)(pTypeKey)
    }
    idPOption map { case (id, p) => new PState[P](MongoId(id), p) }
  }

  protected def keyValQuery(keyVal: KeyVal[P]): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    keyVal.propVals.foreach {
      case (prop, value) => builder += prop.path -> resolvePropVal(prop, value)
    }
    builder.result
  }

  protected def casbahForP(p: P): MongoDBObject = persistentToCasbahTranslator.translate(p)(pTypeKey)

  private def throwDuplicateKeyValException(p: P, cause: DuplicateKeyException): Unit = {
    val indexRegex = """index: (?:[\w\.]*\$)?(\S+)\s+dup key: \{ :""".r.unanchored
    val name = cause.getMessage match {
      case indexRegex(name) => name
      case _ => throw cause
    }
    val key = pType.keySet.find(key => indexName(key) == name).getOrElse(throw cause)
    throw new DuplicateKeyValException(p, key, cause)
  }

  private def resolvePropVal(prop: Prop[P, _], raw: Any): Any = {
    if (shorthandPool.contains(prop.propTypeKey)) {
      def abbreviate[PV : TypeKey] = shorthandPool[PV].abbreviate(raw.asInstanceOf[PV])
      abbreviate(prop.propTypeKey)
    } else if (prop.propTypeKey <:< typeKey[Assoc[_]]) {
      val assoc = raw.asInstanceOf[Assoc[_ <: Persistent]]
      if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      raw.asInstanceOf[MongoId[_ <: Persistent]].objectId
    } else {
      raw
    }
  }

  protected def mongoQuery(query: Query[P]): MongoDBObject = {
    query match {
      case All() => MongoDBObject("$comment" -> "matching Query.All")
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => MongoDBObject(prop.path -> touchupValue(value)(prop.propTypeKey))
        case NeqOp => MongoDBObject(prop.path -> MongoDBObject("$ne" -> touchupValue(value)(prop.propTypeKey)))
      }
      case OrderingQuery(prop, op, value) => op match {
        case LtOp => MongoDBObject(prop.path -> MongoDBObject("$lt" -> touchupValue(value)(prop.propTypeKey)))
        case LteOp => MongoDBObject(prop.path -> MongoDBObject("$lte" -> touchupValue(value)(prop.propTypeKey)))
        case GtOp => MongoDBObject(prop.path -> MongoDBObject("$gt" -> touchupValue(value)(prop.propTypeKey)))
        case GteOp => MongoDBObject(prop.path -> MongoDBObject("$gte" -> touchupValue(value)(prop.propTypeKey)))
      }
      case ConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => MongoDBObject("$and" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
        case OrOp => MongoDBObject("$or" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
      }
    }
  }

  private def touchupValue[A : TypeKey](value: A): Any = {
    val basicResolverOpt = subdomain.getBasicResolver[A]

    val abbreviated = basicResolverOpt match {
      case Some(resolver) => resolver.resolve(value)
      case None => value match {
        case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
        case a => a
      }
    }

    abbreviated match {
      case id: MongoId[_] => id.objectId
      case char: Char => char.toString
      case other => other
    }
  }

}

object MongoRepo {

  def mongoDbFromConfig(config: Config): MongoDB = {
    val mongoClient = MongoClient(config.getString("mongodb.uri"))
    val mongoDb = mongoClient.getDB(config.getString("mongodb.db"))

    import com.mongodb.casbah.commons.conversions.scala._
    RegisterJodaTimeConversionHelpers()

    mongoDb
  }

  def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    session: MongoDB,
    polyRepoOpt: Option[MongoRepo[_ >: P <: Persistent]])
  : MongoRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new MongoRepo(pType, subdomain, session) with PolyMongoRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: MongoRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: MongoRepo[Poly] = poly
          }
          with MongoRepo(pType, subdomain, session) with DerivedMongoRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new MongoRepo(pType, subdomain, session)
    }
    repo.createSchema()
    repo
  }

}
