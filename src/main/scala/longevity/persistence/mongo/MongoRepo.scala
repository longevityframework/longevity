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
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 * @param mongoDb the connection to the mongo database
 */
class MongoRepo[R <: Root : TypeKey] private[persistence] (
  rootType: RootType[R],
  subdomain: Subdomain,
  mongoDb: MongoDB)
extends BaseRepo[R](rootType, subdomain) {
  repo =>

  private[persistence] case class MongoId(objectId: ObjectId) extends PersistedAssoc[R] {
    val associateeTypeKey = repo.rootTypeKey
    private[longevity] val _lock = 0
  }

  private val collectionName = camelToUnderscore(typeName(rootTypeKey.tpe))
  private val mongoCollection = mongoDb(collectionName)
  private val shorthandPool = subdomain.shorthandPool
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)
  private lazy val entityToCasbahTranslator = new EntityToCasbahTranslator(emblemPool, extractorPool, repoPool)
  private lazy val casbahToEntityTranslator = new CasbahToEntityTranslator(emblemPool, extractorPool, repoPool)

  createSchema()

  def create(unpersisted: R) = Future {
    val objectId = new ObjectId()
    val casbah = entityToCasbahTranslator.translate(unpersisted) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.insert(casbah)
    new PState[R](MongoId(objectId), unpersisted)
  }

  def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]] = Future {
    val builder = MongoDBObject.newBuilder
    keyVal.propVals.foreach {
      case (prop, value) => builder += prop.path -> resolvePropVal(prop, value)
    }
    val query = builder.result
    val resultOption = mongoCollection.findOne(query)
    val idRootOption = resultOption map { result =>
      val id = result.getAs[ObjectId]("_id").get
      id -> casbahToEntityTranslator.translate(result)
    }
    idRootOption map { case (id, e) => new PState[R](MongoId(id), e) }
  }

  private def resolvePropVal(prop: Prop[R, _], raw: Any): Any = {
    if (subdomain.shorthandPool.contains(prop.typeKey)) {
      def abbreviate[PV : TypeKey] = subdomain.shorthandPool[PV].abbreviate(raw.asInstanceOf[PV])
      abbreviate(prop.typeKey)
    } else if (prop.typeKey <:< typeKey[Assoc[_]]) {
      val assoc = raw.asInstanceOf[Assoc[_ <: Root]]
      if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      raw.asInstanceOf[MongoRepo[T]#MongoId forSome { type T <: Root }].objectId
    } else {
      raw
    }
  }

  def update(persisted: PState[R]) = Future {
    val root = persisted.get
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = entityToCasbahTranslator.translate(root) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.update(query, casbah)
    new PState[R](persisted.passoc, root)
  }

  def delete(persisted: PState[R]) = Future {
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.remove(query)
    new Deleted(persisted.get)
  }

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] = Future {
    val objectId = assoc.asInstanceOf[MongoId].objectId
    println(s"retrievePersistedAssoc $objectId $rootTypeKey")
    val query = MongoDBObject("_id" -> objectId)
    val resultOption = mongoCollection.findOne(query)
    println(s"retrievePersistedAssoc $resultOption")
    val rootOption = resultOption map { casbahToEntityTranslator.translate(_) }
    rootOption map { e => new PState[R](assoc, e) }
  }

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = Future {
    val cursor: MongoCursor = mongoCollection.find(mongoQuery(query))
    val dbObjs: Seq[DBObject] = cursor.toSeq
    dbObjs.map { result =>
      val id = result.getAs[ObjectId]("_id").get
      val root = casbahToEntityTranslator.translate(result)
      new PState[R](MongoId(id), root)
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

  // this will find a better home in pt #106611128
  private def createSchema(): Unit = {

    rootType.keys.foreach { key =>
      val paths = key.props.map(_.path)
      createMongoIndex(paths, true)
    }

    rootType.indexes.foreach { index =>
      val paths = index.props.map(_.path)
      createMongoIndex(paths, false)
    }

    def createMongoIndex(paths: Seq[String], unique: Boolean): Unit = {
      val mongoPaths = paths map (_ -> 1)
      mongoCollection.createIndex(MongoDBObject(mongoPaths.toList), indexName(paths), unique)
    }

    def indexName(paths: Seq[String]): String = {
      val cappedSegments: Seq[String] = paths.map {
        path => path.split('.').map(_.capitalize).mkString
      }
      s"index${cappedSegments.mkString}"
    }

  }

}
