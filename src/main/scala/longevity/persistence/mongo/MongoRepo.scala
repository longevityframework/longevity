package longevity.persistence.mongo

import com.mongodb.casbah.Imports._
import emblem.imports._
import emblem.stringUtil._
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.ptype._
import longevity.subdomain.ptype.Query._
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

/** a MongoDB repository for persistent entities of type `P`.
 *
 * @param pType the persistent type of the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 * @param mongoDb the connection to the mongo database
 */
private[longevity] class MongoRepo[P <: Persistent : TypeKey] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain,
  mongoDb: MongoDB)
extends BaseRepo[P](pType, subdomain) {
  repo =>

  private val collectionName = camelToUnderscore(typeName(pTypeKey.tpe))
  private val mongoCollection = mongoDb(collectionName)
  private val shorthandPool = subdomain.shorthandPool
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)

  private lazy val entityToCasbahTranslator =
    new PersistentToCasbahTranslator(emblemPool, extractorPool, repoPool)
  private lazy val casbahToEntityTranslator =
      new CasbahToPersistentTranslator(emblemPool, extractorPool, repoPool)

  createSchema()

  def create(p: P)(implicit context: ExecutionContext) = Future {
    val objectId = new ObjectId()
    val casbah = entityToCasbahTranslator.translate(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.insert(casbah)
    new PState[P](MongoId(objectId), p)
  }

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] = Future {
    val cursor: MongoCursor = mongoCollection.find(mongoQuery(query))
    val dbObjs: Seq[DBObject] = cursor.toSeq
    dbObjs.map { result =>
      val id = result.getAs[ObjectId]("_id").get
      val p = casbahToEntityTranslator.translate(result)
      new PState[P](MongoId(id), p)
    }
  }

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    val p = state.get
    val objectId = state.assoc.asInstanceOf[MongoId[P]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = entityToCasbahTranslator.translate(p) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.update(query, casbah)
    new PState[P](state.passoc, p)
  }

  def delete(state: PState[P])(implicit context: ExecutionContext) = Future {
    val objectId = state.assoc.asInstanceOf[MongoId[P]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.remove(query)
    new Deleted(state.get, state.assoc)
  }

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = Future {
    val objectId = assoc.asInstanceOf[MongoId[P]].objectId
    val query = MongoDBObject("_id" -> objectId)
    val resultOption = mongoCollection.findOne(query)
    val pOption = resultOption map { casbahToEntityTranslator.translate(_) }
    pOption map { p => new PState[P](assoc, p) }
  }

  override protected def retrieveByKeyVal(keyVal: KeyVal[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = Future {
    val builder = MongoDBObject.newBuilder
    keyVal.propVals.foreach {
      case (prop, value) => builder += prop.path -> resolvePropVal(prop, value)
    }
    val query = builder.result
    val resultOption = mongoCollection.findOne(query)
    val idPOption = resultOption map { result =>
      val id = result.getAs[ObjectId]("_id").get
      id -> casbahToEntityTranslator.translate(result)
    }
    idPOption map { case (id, p) => new PState[P](MongoId(id), p) }
  }

  private def resolvePropVal(prop: Prop[P, _], raw: Any): Any = {
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

  private def mongoQuery(query: Query[P]): MongoDBObject = {
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
    // TODO seems liek this would fail for a char shorthand. see cassandraRepo
    value match {
      case id: MongoId[_] => id.objectId
      case char: Char => char.toString
      case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
      case _ => value
    }
  }

  // this will find a better home in pt #106611128
  private def createSchema(): Unit = {
    pType.keySet.foreach { key =>
      val paths = key.props.map(_.path)
      createMongoIndex(paths, true)
    }

    val keyProps = pType.keySet.map(_.props)
    pType.indexSet.foreach { index =>
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
