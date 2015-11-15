package longevity.persistence

import emblem.exceptions.CouldNotTransformException
import emblem.imports._
import emblem.traversors.async.Transformer
import emblem.traversors.async.Transformer.CustomTransformer
import emblem.traversors.async.Transformer.CustomTransformerPool
import longevity.exceptions.persistence.BsonTranslationException
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.RootEntity
import longevity.subdomain.UnpersistedAssoc
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

/** traverses an entity graph, replacing every [[longevity.subdomain.UnpersistedAssoc unpersisted assoc]] with a
 * [[longevity.persistence.PersistedAssoc persisted assoc]].
 *
 * used by the [[Repo]] to recursively persist entities.
 *
 * @param repoPool a pool of all the repos in the [[longevity.context.PersistenceContext]]
 * @param emblemPool a pool of emblems for the entities to be transformed
 * @param extractorPool a complete set of the extractors used by the bounded context
 */
private[persistence] class UnpersistedToPersistedTransformer(
  private val repoPool: RepoPool,
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool)
extends Transformer {

  override def transform[A : TypeKey](input: Future[A]): Future[A] = super.transform[A](input) recoverWith {
    case e: CouldNotTransformException => Future.failed(new BsonTranslationException(e.typeKey, e))
  }

  override protected val customTransformers = CustomTransformerPool.empty + transformFutureAssoc

  private lazy val transformFutureAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: Future[B]): Future[B] = {
      val promise = Promise[B]()
      def transformAssoc(assoc: B): Unit = assoc match {
        case persistedAssoc: PersistedAssoc[_] => promise.success(assoc)
        case unpersistedAssoc: UnpersistedAssoc[_] =>
          val unpersistedEntity = assoc.unpersisted
          val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[RootEntity]]
          val repo = repoPool(entityTypeKey)
          val futurePersistedEntity = repo.create(unpersistedEntity).map(_.assoc).asInstanceOf[Future[B]]
          promise.completeWith(futurePersistedEntity)
      }
      input onSuccess { case assoc => transformAssoc(assoc) }
      input onFailure { case e => promise.failure(e) }
      promise.future
    }
  }

}
