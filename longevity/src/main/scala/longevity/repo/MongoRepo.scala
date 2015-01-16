package longevity.repo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.runtime.universe.TypeTag
import scala.util.Failure
import scala.util.Success
import reactivemongo.api.MongoDriver
import reactivemongo.bson._
import emblem._
import emblem.stringUtil._
import longevity.domain._

/** a MongoDB repository for entities of type E */
abstract class MongoRepo[E <: Entity](
  override val entityType: EntityType[E],
  protected val domainShorthands: ShorthandPool = ShorthandPool()
)(
  implicit override val entityTypeTag: TypeTag[E]
) extends Repo[E] {
  repo =>

  // TODO better names for this part of the hierarchy
  case class MongoId(objectId: BSONObjectID) extends Id[E] {
    val associateeTypeTag = repo.entityTypeTag
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this)
  }

  private lazy val collectionName: String = camelToUnderscore(typeName(entityTypeTag.tpe))
  private val mongoCollection = MongoRepo.db.collection(collectionName)

  protected implicit lazy val bsonHandler = new EmblemBsonHandler(entityType.emblem, domainShorthands, repoPool)

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
  // TODO: fix strange compiler problem
}