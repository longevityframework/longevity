package longevity.repo

import scala.language.existentials
import scala.language.higherKinds
import scala.reflect.runtime.universe._
import reactivemongo.bson._
import emblem._
import longevity.domain._

// things i would do if i were not going to replace this class altogether:
// - limit visibility
// - scaladoc
// - unit tests
// - convert to a traversor
// - order methods public/private

class EmblemBsonHandler[E <: Entity : TypeKey](
  private val emblem: Emblem[E],
  private val shorthands: ShorthandPool,
  private val repoPool: RepoPool
)
extends BSONDocumentReader[E] with BSONDocumentWriter[E] {

  def assocHandler[Associatee <: Entity : TypeKey] = new BSONHandler[BSONObjectID, Assoc[Associatee]] {

    // TODO: get rid of asInstanceOf by tightening type on repo pools and repo layers
    lazy val associateeRepo = repoPool(typeKey[Associatee]).asInstanceOf[MongoRepo[Associatee]]

    def read(objectId: BSONObjectID) = associateeRepo.MongoId(objectId)

    // TODO convert class cast into some kind of longevity error
    def write(assoc: Assoc[Associatee]) = assoc.asInstanceOf[associateeRepo.MongoId].objectId
  }

  def assocSetHandler[Associatee <: Entity : TypeKey] = new BSONHandler[BSONArray, Set[Assoc[Associatee]]] {

    def read(bsonArray: BSONArray) = {
      val handler = assocHandlers(assocKey[Associatee])
      val assocHandler = handler.asInstanceOf[BSONHandler[BSONObjectID, Assoc[Associatee]]]
      (0 until bsonArray.length).map { i =>
        val objectId = bsonArray.getAs[BSONObjectID](i).get
        assocHandler.read(objectId)
      }.toSet
    }

    def write(set: Set[Assoc[Associatee]]) = {
      val assocHandler = assocHandlers(assocKey[Associatee])
      val objectIds = set map { assoc => assocHandler.write(assoc) }
      BSONArray(objectIds)
    }
  }

  private def assocKey[Associatee <: Entity : TypeKey] = {
    // TODO: this should be an emblem-supplied one-liner
    implicit val innerTag = typeKey[Associatee].tag
    TypeKey(typeTag[Assoc[Associatee]])
  }

  val BSONCharHandler = new BSONHandler[BSONString, Char] {
    def read(bson: BSONString): Char = bson.value(0)
    def write(char: Char): BSONString = new BSONString(char.toString)
  }

  val BSONFloatHandler = new BSONHandler[BSONDouble, Float] {
    def read(bson: BSONDouble): Float = bson.value.toFloat
    def write(float: Float): BSONDouble = new BSONDouble(float.toDouble)
  }

  private type BsonHandlerFromAny[K] = BSONHandler[_ <: BSONValue, K]
  private type BsonHandlerMap = TypeKeyMap[Any, BsonHandlerFromAny]
  private object BsonHandlerMap { def apply() = TypeKeyMap[Any, BsonHandlerFromAny]() }

  private val baseHandlers = BsonHandlerMap() +
    BSONBooleanHandler +
    BSONCharHandler +
    BSONDoubleHandler +
    BSONFloatHandler +
    BSONIntegerHandler +
    BSONLongHandler +
    BSONStringHandler

  private val assocHandlers = {
    def addAssocHandlerToMap[Associatee <: Entity : TypeKey](map: BsonHandlerMap) = {
      implicit val assocKey2 = assocKey[Associatee]
      map + assocHandler(typeKey[Associatee])
    }
    repoPool.keys.foldLeft(BsonHandlerMap()) { (map, key) =>
      addAssocHandlerToMap(map)(key)
    }
  }

  private val assocSetHandlers = {
    def addAssocHandlerToMap[Associatee <: Entity : TypeKey](map: BsonHandlerMap) = {
      implicit val setKey = assocSetKey[Associatee]
      map + assocSetHandler(typeKey[Associatee])
    }
    repoPool.keys.foldLeft(BsonHandlerMap()) { (map, key) =>
      addAssocHandlerToMap(map)(key)
    }
  }

  private def assocSetKey[Associatee <: Entity : TypeKey] = {
    // TODO: this should be an emblem-supplied one-liner
    implicit val innerTag = typeKey[Associatee].tag
    TypeKey(typeTag[Set[Assoc[Associatee]]])
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
