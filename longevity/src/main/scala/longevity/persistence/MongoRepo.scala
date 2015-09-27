package longevity.persistence

import com.mongodb.casbah.Imports._
import emblem.imports._
import emblem.stringUtil._
import longevity.subdomain._
import org.bson.types.ObjectId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

/** a MongoDB repository for aggregate roots of type `E`.
 *
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 */
class MongoRepo[E <: RootEntity : TypeKey] protected[persistence] (
  entityType: RootEntityType[E],
  emblemPool: EmblemPool,
  shorthandPool: ShorthandPool)
extends Repo[E](
  entityType,
  emblemPool,
  shorthandPool) {
  repo =>

  private[persistence] case class MongoId(objectId: ObjectId) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get.get)
  }

  private val collectionName = camelToUnderscore(typeName(entityTypeKey.tpe))
  private val mongoCollection = MongoRepo.mongoDb(collectionName)

  private val extractorPool = shorthandPoolToExtractorPool(shorthandPool)
  private lazy val entityToCasbahTranslator = new EntityToCasbahTranslator(emblemPool, extractorPool, repoPool)
  private lazy val casbahToEntityTranslator = new CasbahToEntityTranslator(emblemPool, extractorPool, repoPool)

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted.get) map { patched =>
      val objectId = new ObjectId()
      val casbah = entityToCasbahTranslator.translate(patched) ++ MongoDBObject("_id" -> objectId)
      val writeResult = mongoCollection.insert(casbah)
      new Persisted[E](MongoId(objectId), patched)
    }
  })

  def retrieve(natKey: NatKey[E])(natKeyVal: natKey.Val): Future[Option[Persisted[E]]] = Future {
    val builder = MongoDBObject.newBuilder
    natKey.props.foreach { prop => builder += (prop.path -> natKeyVal.shorthand(prop)) }
    val query = builder.result
    val resultOption = mongoCollection.findOne(query)
    val idEntityOption = resultOption map { result =>
      val id = result.getAs[ObjectId]("_id").get
      id -> casbahToEntityTranslator.translate(result)
    }
    idEntityOption map { case (id, e) => new Persisted[E](MongoId(id), e) }
  }

  def update(persisted: Persisted[E]) = patchUnpersistedAssocs(persisted.get) map { patched =>
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val casbah = entityToCasbahTranslator.translate(patched) ++ MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.update(query, casbah)
    new Persisted[E](persisted.assoc, patched)
  }

  def delete(persisted: Persisted[E]) = Future {
    val objectId = persisted.assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val writeResult = mongoCollection.remove(query)
    new Deleted(persisted)
  }

  private def retrieve(assoc: PersistedAssoc[E]) = Future {
    val objectId = assoc.asInstanceOf[MongoId].objectId
    val query = MongoDBObject("_id" -> objectId)
    val resultOption = mongoCollection.findOne(query)
    val entityOption = resultOption map { casbahToEntityTranslator.translate(_) }
    entityOption map { e => new Persisted[E](assoc, e) }
  }

}

object MongoRepo {

  // TODO pt-84759650 move this stuff to context config
  val mongoClient = MongoClient("localhost", 27017)
  val mongoDb = mongoClient("test")

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterJodaTimeConversionHelpers()
}
