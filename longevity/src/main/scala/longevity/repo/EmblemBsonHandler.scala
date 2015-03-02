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
      repoPool.repoForEntityTypeKey(typeKey[Associatee]).asInstanceOf[MongoRepo[Associatee]]

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
      map(key).asInstanceOf[BSONHandler[_ <: BSONValue, K]]

    def +[K : TypeKey](value: BSONHandler[_ <: BSONValue, K]): BsonHandlerMap =
      new BsonHandlerMap(map + (typeKey[K] -> value))

    def ++(thatHandlerMap: BsonHandlerMap): BsonHandlerMap = new BsonHandlerMap(map ++ thatHandlerMap.map)

    override def toString = map.keys.toString
  }

  private val baseHandlers = new BsonHandlerMap() + BSONBooleanHandler + BSONDoubleHandler +
    BSONIntegerHandler + BSONLongHandler + BSONStringHandler

  private val assocHandlers = {
    def assocTag[Associatee <: Entity](implicit tag: TypeTag[Associatee]) = typeTag[Assoc[Associatee]]
    def addAssocHandlerToMap[Associatee <: Entity : TypeTag](tag: TypeTag[Associatee], map: BsonHandlerMap) = {
      implicit val key: TypeKey[Assoc[Associatee]] = TypeKey(assocTag(tag))
      map + assocHandler(tag)
    }
    repoPool.entityTypeKeys.foldLeft(new BsonHandlerMap()) { (map, key) =>
      addAssocHandlerToMap(key.tag, map)
    }
  }

  private val assocSetHandlers = {
    def assocTag[Associatee <: Entity](implicit tag: TypeTag[Associatee]) = typeTag[Assoc[Associatee]]
    def assocSetTag[Associatee <: Entity](implicit tag: TypeTag[Associatee]) = typeTag[Set[Assoc[Associatee]]]
    def addAssocHandlerToMap[Associatee <: Entity : TypeTag](tag: TypeTag[Associatee], map: BsonHandlerMap) = {
      implicit val key: TypeKey[Set[Assoc[Associatee]]] = TypeKey(assocSetTag(tag))
      map + assocSetHandler(tag)
    }
    repoPool.entityTypeKeys.foldLeft(new BsonHandlerMap()) { (map, key) =>
      addAssocHandlerToMap(key.tag, map)
    }
  }

  private val handlerMap = baseHandlers ++ assocHandlers ++ assocSetHandlers

  def read(bson: BSONDocument): E = {
    val builder = emblem.builder()
    emblem.props.foreach { prop => setProp(bson, builder, prop) }
    builder.build()
  }

  // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
  private def setProp[U](bson: BSONDocument, builder: HasEmblemBuilder[E], prop: EmblemProp[E, U]): Unit = {
    val propVal = shorthands.get(prop.typeKey) match {
      case Some(shorthand) => getPropValFromShorthand(bson, shorthand, prop.name)
      case None => getPropVal(bson, prop.typeKey, prop.name)
    }
    builder.setProp(prop, propVal)
  }

  private def getPropValFromShorthand[Actual, Abbreviated](
    bson: BSONDocument, shorthand: Shorthand[Actual, Abbreviated], name: String): Actual = {
    val propVal = getPropVal[Abbreviated](bson, shorthand.abbreviatedTypeKey, name)
    shorthand.unabbreviate(propVal)
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
    shorthands.get(prop.typeKey) match {
      case Some(shorthand) => bsonValueForShorthand(e, prop, shorthand)
      case None => {
        val propVal = prop.get(e)
        val handler = handlerMap(prop.typeKey)
        handler.write(propVal)
      }
    }
  }

  private def bsonValueForShorthand[Actual, Abbreviated](
    e: E, prop: EmblemProp[E, Actual], shorthand: Shorthand[Actual, Abbreviated]): BSONValue = {
    val propVal: Abbreviated = shorthand.abbreviate(prop.get(e))
    val key: TypeKey[Abbreviated] = shorthand.abbreviatedTypeKey
    val handler = handlerMap(key)
    handler.write(propVal)
  }

}
