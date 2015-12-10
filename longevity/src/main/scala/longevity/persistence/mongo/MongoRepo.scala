package longevity.persistence.mongo

import com.mongodb.casbah.Imports._
import emblem.imports._
import emblem.stringUtil._
import longevity.exceptions.subdomain.AssocIsUnpersistedException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.subdomain.root.Query._
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

/** a MongoDB repository for aggregate roots of type `R`.
 *
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 * @param mongoDb the connection to the mongo database
 */
class MongoRepo[R <: RootEntity : TypeKey] protected[persistence] (
  entityType: RootEntityType[R],
  subdomain: Subdomain,
  mongoDb: MongoDB)
extends Repo[R](entityType, subdomain) {
  repo =>

  private[persistence] case class MongoId(objectId: ObjectId) extends PersistedAssoc[R] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get)
  }

  private val collectionName = camelToUnderscore(typeName(entityTypeKey.tpe))
  private val mongoCollection = mongoDb(collectionName)
  private val shorthandPool = subdomain.shorthandPool
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)
  private lazy val entityToCasbahTranslator = new EntityToCasbahTranslator(emblemPool, extractorPool, repoPool)
  private lazy val casbahToEntityTranslator = new CasbahToEntityTranslator(emblemPool, extractorPool, repoPool)

  createSchema()

  def create(unpersisted: Unpersisted[R]) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted.get) map { patched =>
      val objectId = new ObjectId()
      val casbah = entityToCasbahTranslator.translate(patched) ++ MongoDBObject("_id" -> objectId)
      val writeResult = mongoCollection.insert(casbah)
      new Persisted[R](MongoId(objectId), patched)
    }
  })

  def retrieve(keyVal: KeyVal[R]): Future[Option[Persisted[R]]] = Future {
    val builder = MongoDBObject.newBuilder
    keyVal.propVals.foreach {
      case (prop, value) => builder += prop.path -> resolvePropVal(prop, value)
    }
    val query = builder.result
    val resultOption = mongoCollection.findOne(query)
    val idEntityOption = resultOption map { result =>
      val id = result.getAs[ObjectId]("_id").get
      id -> casbahToEntityTranslator.translate(result)
    }
    idEntityOption map { case (id, e) => new Persisted[R](MongoId(id), e) }
  }

  private def resolvePropVal(prop: Prop[R, _], raw: Any): Any = {
    if (subdomain.shorthandPool.contains(prop.typeKey)) {
      def abbreviate[PV : TypeKey] = subdomain.shorthandPool[PV].abbreviate(raw.asInstanceOf[PV])
      abbreviate(prop.typeKey)
    } else if (prop.typeKey <:< typeKey[Assoc[_]]) {
      val assoc = raw.asInstanceOf[Assoc[_ <: RootEntity]]
      if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      raw.asInstanceOf[MongoRepo[T]#MongoId forSome { type T <: RootEntity }].objectId
    } else {
      raw
    }
  }

  def update(persisted: Persisted[R]) = patchUnpersistedAssocs(persisted.get) map { patched =>
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = entityToCasbahTranslator.translate(patched) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.update(query, casbah)
    new Persisted[R](persisted.assoc, patched)
  }

  def delete(persisted: Persisted[R]) = Future {
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.remove(query)
    new Deleted(persisted)
  }

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[Persisted[R]]] = Future {
    val cursor: MongoCursor = mongoCollection.find(mongoQuery(query))
    val dbObjs: Seq[DBObject] = cursor.toSeq
    dbObjs.map { result =>
      val id = result.getAs[ObjectId]("_id").get
      val root = casbahToEntityTranslator.translate(result)
      new Persisted[R](MongoId(id), root)
    }
  }

  private def mongoQuery(query: ValidatedQuery[R]): MongoDBObject = {
    query match {
      case VEqualityQuery(prop, op, value) => op match {
        case EqOp => MongoDBObject(prop.path -> touchupValue(value)(prop.typeKey))
        case NeqOp => MongoDBObject(prop.path -> MongoDBObject("$ne" -> touchupValue(value)(prop.typeKey)))
      }
      case VOrderingQuery(prop, op, value) => op match {
        case LtOp => MongoDBObject(prop.path -> MongoDBObject("$lt" -> touchupValue(value)(prop.typeKey)))
        case LteOp => MongoDBObject(prop.path -> MongoDBObject("$lte" -> touchupValue(value)(prop.typeKey)))
        case GtOp => MongoDBObject(prop.path -> MongoDBObject("$gt" -> touchupValue(value)(prop.typeKey)))
        case GteOp => MongoDBObject(prop.path -> MongoDBObject("$gte" -> touchupValue(value)(prop.typeKey)))
      }
      case VConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => MongoDBObject("$and" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
        case OrOp => MongoDBObject("$or" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
      }
    }
  }

  private def touchupValue[A : TypeKey](value: A): Any = {
    value match {
      case id: MongoRepo[_]#MongoId => id.objectId
      case char: Char => char.toString
      case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
      case _ => value
    }
  }

  private def retrieve(assoc: PersistedAssoc[R]) = Future {
    val objectId = assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val resultOption = mongoCollection.findOne(query)
    val entityOption = resultOption map { casbahToEntityTranslator.translate(_) }
    entityOption map { e => new Persisted[R](assoc, e) }
  }

  // this will find a better home in pt #106611128
  private def createSchema(): Unit = {

    entityType.keys.foreach { key =>
      val paths = key.props.map(_.path)
      createMongoIndex(paths, true)
    }

    entityType.indexes.foreach { index =>
      val paths = index.props.map(_.path)
      createMongoIndex(paths, false)
    }

    def createMongoIndex(paths: Seq[String], unique: Boolean): Unit = {
      val mongoPaths = paths map (_ -> 1)
      mongoCollection.createIndex(MongoDBObject(mongoPaths.toList), indexName(paths), unique)
    }

    def indexName(paths: Seq[String]): String = {
      val cappedSegments: Seq[String] = paths.map {
        path => path.split('.').map(_.capitalize).mkString("")
      }
      s"index${cappedSegments.mkString}"
    }

  }

}
