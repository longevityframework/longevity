package longevity.persistence.mongo

import com.mongodb.casbah.Imports._
import emblem.imports._
import emblem.stringUtil._
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.subdomain.root.Query._
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

// TODO this class needs refactor

/** a MongoDB repository for aggregate roots of type `R`.
 *
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 * @param mongoDb the connection to the mongo database
 */
private[longevity] class MongoRepo[R <: Root : TypeKey] private[persistence] (
  rootType: RootType[R],
  subdomain: Subdomain,
  mongoDb: MongoDB)
extends BaseRepo[R](rootType, subdomain) {
  repo =>

  private val collectionName = camelToUnderscore(typeName(rootTypeKey.tpe))
  private val mongoCollection = mongoDb(collectionName)
  private val shorthandPool = subdomain.shorthandPool
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)

  private lazy val entityToCasbahTranslator =
    new EntityToCasbahTranslator(emblemPool, extractorPool, repoPool)
  private lazy val casbahToEntityTranslator =
      new CasbahToEntityTranslator(emblemPool, extractorPool, repoPool)

  createSchema()

  def create(unpersisted: R)(implicit context: ExecutionContext) = Future {
    val objectId = new ObjectId()
    val casbah = entityToCasbahTranslator.translate(unpersisted) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.insert(casbah)
    new PState[R](MongoId(objectId), unpersisted)
  }

  def retrieveByQuery(query: Query[R])(implicit context: ExecutionContext)
  : Future[Seq[PState[R]]] = Future {
    val cursor: MongoCursor = mongoCollection.find(mongoQuery(query))
    val dbObjs: Seq[DBObject] = cursor.toSeq
    dbObjs.map { result =>
      val id = result.getAs[ObjectId]("_id").get
      val root = casbahToEntityTranslator.translate(result)
      new PState[R](MongoId(id), root)
    }
  }

  def update(persisted: PState[R])(implicit context: ExecutionContext) = Future {
    val root = persisted.get
    val objectId = persisted.assoc.asInstanceOf[MongoId[R]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = entityToCasbahTranslator.translate(root) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.update(query, casbah)
    new PState[R](persisted.passoc, root)
  }

  def delete(persisted: PState[R])(implicit context: ExecutionContext) = Future {
    val objectId = persisted.assoc.asInstanceOf[MongoId[R]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.remove(query)
    new Deleted(persisted.get, persisted.assoc)
  }

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[R])(
    implicit context: ExecutionContext)
  : Future[Option[PState[R]]] = Future {
    val objectId = assoc.asInstanceOf[MongoId[R]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val resultOption = mongoCollection.findOne(query)
    val rootOption = resultOption map { casbahToEntityTranslator.translate(_) }
    rootOption map { e => new PState[R](assoc, e) }
  }

  override protected def retrieveByKeyVal(keyVal: KeyVal[R])(implicit context: ExecutionContext)
  : Future[Option[PState[R]]] = Future {
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
      raw.asInstanceOf[MongoId[_ <: Root]].objectId
    } else {
      raw
    }
  }

  private def mongoQuery(query: Query[R]): MongoDBObject = {
    query match {
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => MongoDBObject(prop.path -> touchupValue(value)(prop.typeKey))
        case NeqOp => MongoDBObject(prop.path -> MongoDBObject("$ne" -> touchupValue(value)(prop.typeKey)))
      }
      case OrderingQuery(prop, op, value) => op match {
        case LtOp => MongoDBObject(prop.path -> MongoDBObject("$lt" -> touchupValue(value)(prop.typeKey)))
        case LteOp => MongoDBObject(prop.path -> MongoDBObject("$lte" -> touchupValue(value)(prop.typeKey)))
        case GtOp => MongoDBObject(prop.path -> MongoDBObject("$gt" -> touchupValue(value)(prop.typeKey)))
        case GteOp => MongoDBObject(prop.path -> MongoDBObject("$gte" -> touchupValue(value)(prop.typeKey)))
      }
      case ConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => MongoDBObject("$and" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
        case OrOp => MongoDBObject("$or" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
      }
    }
  }

  private def touchupValue[A : TypeKey](value: A): Any = {
    value match {
      case id: MongoId[_] => id.objectId
      case char: Char => char.toString
      case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
      case _ => value
    }
  }

  // this will find a better home in pt #106611128
  private def createSchema(): Unit = {
    rootType.keySet.foreach { key =>
      val paths = key.props.map(_.path)
      createMongoIndex(paths, true)
    }

    val keyProps = rootType.keySet.map(_.props)
    rootType.indexSet.foreach { index =>
      if (!keyProps.contains(index.props)) {
        val paths = index.props.map(_.path)
        createMongoIndex(paths, false)
      }
    }
  }

  private def createMongoIndex(paths: Seq[String], unique: Boolean): Unit = {
    val mongoPaths = paths map (_ -> 1)
    mongoCollection.createIndex(MongoDBObject(mongoPaths.toList), indexName(paths, unique), unique)
  }

  private def indexName(paths: Seq[String], unique: Boolean): String = {
    val cappedSegments: Seq[String] = paths.map {
      path => path.split('.').map(_.capitalize).mkString
    }
    val prefix = if (unique) "key" else "index"
    s"${prefix}_${cappedSegments.mkString}"
  }

}
