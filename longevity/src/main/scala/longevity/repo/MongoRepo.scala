package longevity.repo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success
import reactivemongo.api.MongoDriver
import reactivemongo.bson._
import emblem._
import emblem.stringUtil._
import longevity.domain._
import longevity.context.BoundedContext

/** a MongoDB repository for aggregate roots of type E */
class MongoRepo[E <: RootEntity : TypeKey](
  override val entityType: RootEntityType[E],
  protected val boundedContext: BoundedContext)
extends Repo[E] {
  repo =>

  protected[longevity] case class MongoId(objectId: BSONObjectID) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get.get)
  }

  private lazy val collectionName: String = camelToUnderscore(typeName(entityTypeKey.tpe))
  private val mongoCollection = MongoRepo.db.collection(collectionName)

  protected implicit lazy val bsonHandler =
    new EmblemBsonHandler(entityType.emblem, boundedContext.shorthandPool, repoPool)

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    for (
      patched <- patchUnpersistedAssocs(unpersisted.e);
      id = BSONObjectID.generate;
      document = BSON.writeDocument(patched).add(BSONDocument("_id" -> id));
      lastError <- mongoCollection.insert(document)
    ) yield Persisted[E](MongoId(id), patched)
  })

  def retrieve(id: PersistedAssoc[E]) = {
    val objectId = id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)
    val future = mongoCollection.find(selector).one[E]
    future map { resultOption => resultOption.map( Persisted[E](id, _) ) }
  }

  def update(persisted: Persisted[E]) = {
    val objectId = persisted.id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)
    for (
      patched <- patchUnpersistedAssocs(persisted.curr);
      document = BSON.writeDocument(patched).add(BSONDocument("_id" -> objectId));
      lastError <- mongoCollection.update(selector, document)
    ) yield Persisted[E](persisted.id, patched)
  }

  def delete(persisted: Persisted[E]) = {
    val objectId = persisted.id.asInstanceOf[MongoId].objectId
    val selector = BSONDocument("_id" -> objectId)
    val future = mongoCollection.remove(selector)
    future map { lastError => Deleted(persisted) }
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
