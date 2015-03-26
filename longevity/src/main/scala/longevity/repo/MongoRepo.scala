package longevity.repo

import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.concurrent.Future
import scala.util.Success
import com.mongodb.casbah.Imports._
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

  protected[longevity] case class MongoId(objectId: ObjectId) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get.get)
  }

  private val collectionName = camelToUnderscore(typeName(entityTypeKey.tpe))
  private val mongoCollection = MongoRepo.mongoDb(collectionName)
  private val entityToCasbahTranslator = new EntityToCasbah(boundedContext)
  private val casbahToEntityTranslator = new CasbahToEntityTranslator(boundedContext)

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted.e) map { patched =>
      val objectId = new ObjectId();
      val casbah = entityToCasbahTranslator.translate(patched) ++ MongoDBObject("_id" -> objectId)
      val writeResult = mongoCollection.insert(casbah)
      Persisted[E](MongoId(objectId), patched)
    }
  })

  def retrieve(id: PersistedAssoc[E]) = {
    Future {
      val objectId = id.asInstanceOf[MongoId].objectId
      val query = MongoDBObject("_id" -> objectId)
      val resultOption = mongoCollection.findOne(query)
      val entityOption = resultOption map { casbahToEntityTranslator.translate(_) }
      entityOption map { e => Persisted[E](id, e) }
    }
  }

  def update(persisted: Persisted[E]) = {
    val objectId = persisted.id.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    for (
      patched <- patchUnpersistedAssocs(persisted.curr);
      casbahObject = entityToCasbahTranslator.translate(patched) ++ query;
      writeResult = mongoCollection.update(query, casbahObject)
    ) yield Persisted[E](persisted.id, patched)
  }

  def delete(persisted: Persisted[E]) = {
    Future {
      val objectId = persisted.id.asInstanceOf[MongoId].objectId
      val query = MongoDBObject("_id" -> objectId)
      val writeResult = mongoCollection.remove(query)
      Deleted(persisted)
    }
  }

}

object MongoRepo {

  // TODO: move this stuff to context config
  val mongoClient = MongoClient("localhost", 27017)
  val mongoDb = mongoClient("test")

}
