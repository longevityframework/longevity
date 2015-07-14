package longevity.persistence

import emblem.imports._
import emblem.traversors.async.Transformer
import emblem.traversors.async.Transformer.CustomTransformer
import emblem.traversors.async.Transformer.CustomTransformerPool
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.RootEntity
import longevity.subdomain.UnpersistedAssoc
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

/** traverses an entity graph, replacing every [[longevity.persistence.PersistedAssoc persisted assoc]] with an
 * [[longevity.subdomain.UnpersistedAssoc unpersisted assoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 *
 * @param emblemPool a pool of emblems for the entities to be transformed
 * @param extractorPool a complete set of the extractors used by the bounded context
 */
private[persistence] class UnpersistedToPersistedTransformer(
  private val repoPool: RepoPool,
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool)
extends Transformer {

  override protected val customTransformers = CustomTransformerPool.empty + transformAssoc

  private lazy val transformAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: Future[B]): Future[B] = {
      val promise = Promise[B]()
      // TODO better variable name than b
      def completeB(b: B): Unit = b match {
        case persistedAssoc: PersistedAssoc[_] => promise.success(b)
        case unpersistedAssoc: UnpersistedAssoc[_] =>
          val unpersistedEntity = b.unpersisted
          val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[RootEntity]]
          val repo = repoPool(entityTypeKey)
          val futurePersistedEntity = repo.create(unpersistedEntity).map(_.id).asInstanceOf[Future[B]]
          promise.completeWith(futurePersistedEntity)
      }
      input.onComplete { tryB =>
        tryB match {
          case Success(b) => completeB(b)
          case Failure(e) => promise.failure(e)
        }
      }
      promise.future
    }
  }

}
