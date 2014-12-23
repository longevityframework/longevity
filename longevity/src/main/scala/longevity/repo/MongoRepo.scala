package longevity.repo

import scala.reflect.runtime.universe.TypeTag
import scala.util.Failure
import scala.util.Success
import reactivemongo.api.MongoDriver
import reactivemongo.bson._
import longevity.domain._

/** a MongoDB repository for entities of type E */
abstract class MongoRepo[E <: Entity](
  override val entityType: EntityType[E]
)(
  implicit override val entityTypeTag: TypeTag[E]
) extends Repo[E] {
  repo =>

  // TODO better names for this part of the hierarchy
  case class MongoId(objectId: BSONObjectID) extends Id[E] {
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this)
  }

  // TODO: find a decent place for these three functions

  // based on capitalize in scala library StringLike.scala
  /** Returns this string with first character converted to lower case.
   * If the first character of the string is lower case, the string is returned unchanged.
   */
  def uncapitalize(s: String): String =
    if (s == null) null
    else if (s.length == 0) ""
    else if (s.charAt(0).isLower) toString
    else {
      val chars = s.toCharArray
      chars(0) = chars(0).toLower
      new String(chars)
    }

  // TODO: attribute https://gist.github.com/sidharthkuruvila/3154845
  /**
   * Takes a camel cased identifier name and returns an underscore separated
   * name
   *
   * Example:
   *     camelToUnderscores("thisIsA1Test") == "this_is_a_1_test"
   */
  def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(uncapitalize(name), { m =>
    "_" + m.group(0).toLowerCase()
  })
 
  /* 
   * Takes an underscore separated identifier name and returns a camel cased one
   *
   * Example:
   *    underscoreToCamel("this_is_a_1_test") == "thisIsA1Test"
   */
  def underscoreToCamel(name: String) = "_([a-z\\d])".r.replaceAllIn(uncapitalize(name), { m =>
    m.group(1).toUpperCase()
  })

  import scala.concurrent.ExecutionContext.Implicits.global
  private lazy val collectionName: String = camelToUnderscores(entityTypeTag.tpe.typeSymbol.name.decodedName.toString)
  private val mongoCollection = MongoRepo.db.collection(collectionName)

  implicit def assocHandler[Associatee <: Entity : TypeTag]
  : BSONHandler[BSONObjectID, Assoc[Associatee]] = new BSONHandler[BSONObjectID, Assoc[Associatee]] {

    // TODO: get rid of asInstanceOf by tightening type on repo pools and repo layers
    lazy val associateeRepo =
      repoPool.repoForEntityTypeTag(implicitly[TypeTag[Associatee]]).asInstanceOf[MongoRepo[Associatee]]

    def read(objectId: BSONObjectID) = associateeRepo.MongoId(objectId)

    // TODO convert class cast into some kind of longevity error
    def write(assoc: Assoc[Associatee]) = assoc.asInstanceOf[associateeRepo.MongoId].objectId
  }

  protected implicit val bsonHandler: BSONDocumentReader[E] with BSONDocumentWriter[E]

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    val e = patchUnpersistedAssocs(unpersisted.e)
    val id = BSONObjectID.generate
    val document = BSON.writeDocument(e).add(BSONDocument("_id" -> id))

    val future = mongoCollection.insert(document)

    // TODO handle errors for real
    import scala.concurrent.Await
    import scala.concurrent.duration._
    val lastError = Await.result(future, 10.seconds)

    Persisted[E](MongoId(id), e)
  })

  def retrieve(id: Id[E]) = {
    val objectId = id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)

    val future = mongoCollection.find(selector).one[E]

    // TODO handle futures appropriately
    import scala.concurrent.Await
    import scala.concurrent.duration._
    val lastError = Await.result(future, 10.seconds)

    // TODO: okay, but an error here could indicate something else, like network problem
    lastError.map(Persisted[E](id, _)).getOrElse(NotFound(id))
  }

  def update(persisted: Persisted[E]) = {
    val objectId = persisted.id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)
    val patchedEntity = patchUnpersistedAssocs(persisted.curr)
    val document = BSON.writeDocument(patchedEntity).add(BSONDocument(
      "_id" -> objectId))
    val future = mongoCollection.update(selector, document)

    // TODO handle errors for real
    import scala.concurrent.Await
    import scala.concurrent.duration._
    val lastError = Await.result(future, 10.seconds)

    Persisted[E](persisted.id, persisted.curr)
  }

  def delete(persisted: Persisted[E]) = {
    val objectId = persisted.id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)

    val future = mongoCollection.remove(selector)

    import scala.concurrent.Await
    import scala.concurrent.duration._
    val lastError = Await.result(future, 10.seconds)

    Deleted(persisted)
  }

}

object MongoRepo {
  // TODO: move 4 lines to MongoSessionManager
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  import scala.concurrent.ExecutionContext.Implicits.global
  val db = connection.db("test")
  // TODO: fix hanging session problem
}
