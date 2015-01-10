package longevity.repo

import scala.language.existentials
import scala.language.higherKinds
import scala.reflect.runtime.universe._
import reactivemongo.bson._
import emblem._
import longevity.domain._

// TODO scaladoc
// TODO unit tests
// TODO refactor for better naming

class EmblemBsonHandler[E <: Entity :TypeTag](
  private val emblem: Emblem[E],
  private val shorthands: ShorthandPool,
  private val repoPool: RepoPool
)
extends BSONDocumentReader[E] with BSONDocumentWriter[E] {

  def assocHandler[Associatee <: Entity : TypeTag] = new BSONHandler[BSONObjectID, Assoc[Associatee]] {

    // TODO: get rid of asInstanceOf by tightening type on repo pools and repo layers
    lazy val associateeRepo =
      repoPool.repoForEntityTypeTag(implicitly[TypeTag[Associatee]]).asInstanceOf[MongoRepo[Associatee]]

    def read(objectId: BSONObjectID) = associateeRepo.MongoId(objectId)

    // TODO convert class cast into some kind of longevity error
    def write(assoc: Assoc[Associatee]) = assoc.asInstanceOf[associateeRepo.MongoId].objectId
  }

  def assocSetHandler[Associatee <: Entity : TypeTag] = new BSONHandler[BSONArray, Set[Assoc[Associatee]]] {

    def read(bsonArray: BSONArray) = {
      val handler = assocHandlers(TypeKey(typeTag[Assoc[Associatee]]))
      val assocHandler = handler.asInstanceOf[BSONHandler[BSONObjectID, Assoc[Associatee]]]
      (0 until bsonArray.length).map { i =>
        val objectId = bsonArray.getAs[BSONObjectID](i).get
        assocHandler.read(objectId)
      }.toSet
    }

    def write(set: Set[Assoc[Associatee]]) = {
      val assocHandler = assocHandlers(TypeKey(typeTag[Assoc[Associatee]]))
      val objectIds = set map { assoc => assocHandler.write(assoc) }
      BSONArray(objectIds)
    }
  }

  private class BsonHandlerMap(private val map: Map[TypeKey[_], BSONHandler[_, _]] = Map()) {

    def apply[K](key: TypeKey[K]): BSONHandler[_ <: BSONValue, K] =
    {
      if (!map.contains(key)) {
        println(s"couldnt find key $key in \n\n$this")
      }
      map(key).asInstanceOf[BSONHandler[_ <: BSONValue, K]]
    }

    def +[K : TypeKey](value: BSONHandler[_ <: BSONValue, K]): BsonHandlerMap =
      new BsonHandlerMap(map + (typeKey[K] -> value))

    def ++(thatHandlerMap: BsonHandlerMap): BsonHandlerMap = new BsonHandlerMap(map ++ thatHandlerMap.map)

    override def toString = map.keys.toString
  }

  private val baseHandlers = new BsonHandlerMap() + BSONBooleanHandler + BSONDoubleHandler +
    BSONIntegerHandler + BSONLongHandler + BSONStringHandler

  private val assocHandlers = {
    def assocTag[E <: Entity](implicit tag: TypeTag[E]) = typeTag[Assoc[E]]
    def addAssocHandlerToMap[E <: Entity : TypeTag](tag: TypeTag[E], map: BsonHandlerMap) = {
      implicit val key: TypeKey[Assoc[E]] = TypeKey(assocTag(tag))
      map + assocHandler(tag)
    }
    repoPool.entityTypeTags.foldLeft(new BsonHandlerMap()) { (map, tag) =>
      addAssocHandlerToMap(tag, map)
    }
  }

  private val assocSetHandlers = {
    def assocTag[E <: Entity](implicit tag: TypeTag[E]) = typeTag[Assoc[E]]
    def assocSetTag[E <: Entity](implicit tag: TypeTag[E]) = typeTag[Set[Assoc[E]]]
    def addAssocHandlerToMap[E <: Entity : TypeTag](tag: TypeTag[E], map: BsonHandlerMap) = {
      implicit val key: TypeKey[Set[Assoc[E]]] = TypeKey(assocSetTag(tag))
      println(s"addAssocHandlerToMap $tag $key")
      map + assocSetHandler(tag)
    }
    repoPool.entityTypeTags.foldLeft(new BsonHandlerMap()) { (map, tag) =>
      addAssocHandlerToMap(tag, map)
    }
  }

  private val handlerMap = baseHandlers ++ assocHandlers ++ assocSetHandlers

  def read(bson: BSONDocument): E = {
    emblem.props.foldLeft(emblem.nullInstance) { (e, prop) => setProp(bson, e, prop) }
  }

  private def setProp[U](bson: BSONDocument, e: E, prop: EmblemProp[E, U]): E = {
    val propVal = shorthands.longTypeKeyToShorthand(prop.typeKey) match {
      case Some(shorthand) => getPropValFromShorthand(bson, shorthand, prop.name)
      case None => getPropVal(bson, prop.typeKey, prop.name)
    }
    prop.set(e, propVal)
  }

  private def getPropValFromShorthand[Long, Short](
    bson: BSONDocument, shorthand: Shorthand[Long, Short], name: String): Long = {
    val propVal = getPropVal[Short](bson, shorthand.shortTypeKey, name)
    shorthand.unshorten(propVal)
  }

  private def getPropVal[U](bson: BSONDocument, typeKey: TypeKey[U], name: String): U = {
    implicit val handler = handlerMap(typeKey)
    bson.getAs[U](name).get    
  }

  def write(e: E): BSONDocument = {
    val elements = emblem.props.toStream.map { prop =>
      (prop.name, bsonValueForProp(e, prop))
    }
    BSONDocument(elements)
  }

  private def bsonValueForProp[U](e: E, prop: EmblemProp[E, U]): BSONValue = {
    shorthands.longTypeKeyToShorthand(prop.typeKey) match {
      case Some(shorthand) => bsonValueForShorthand(e, prop, shorthand)
      case None => {
        val propVal = prop.get(e)
        val handler = handlerMap(prop.typeKey)
        handler.write(propVal)
      }
    }
  }

  private def bsonValueForShorthand[Long, Short](
    e: E, prop: EmblemProp[E, Long], shorthand: Shorthand[Long, Short]): BSONValue = {
    val propVal: Short = shorthand.shorten(prop.get(e))
    val key: TypeKey[Short] = shorthand.shortTypeKey
    val handler = handlerMap(key)
    handler.write(propVal)
  }

}
