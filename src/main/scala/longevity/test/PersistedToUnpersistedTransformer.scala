package longevity.test

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import emblem.traversors.async.Transformer
import emblem.traversors.async.Transformer.CustomTransformer
import emblem.traversors.async.Transformer.CustomTransformerPool
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.Root
import longevity.persistence.RepoPool

/** traverses an entity graph, replacing every [[longevity.persistence.PersistedAssoc persisted assoc]] with an
 * [[longevity.subdomain.UnpersistedAssoc unpersisted assoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 *
 * @param emblemPool a pool of emblems for the entities to be transformed
 * @param extractorPool a complete set of the extractors used by the bounded context
 * @param repoPool the repo pool to look up associations with
 */
private[test] class PersistedToUnpersistedTransformer(
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool,
  private val repoPool: RepoPool)
extends Transformer {

  override protected val customTransformers = CustomTransformerPool.empty + transformAssoc

  private lazy val transformAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: Future[B]): Future[B] =
      input.flatMap { b =>
        def unpersistedRoot[R <: Root : TypeKey]: Future[R] = {
          println(s"unpersistedRoot $b ${typeKey[R]}")
          val assoc = b.asInstanceOf[Assoc[R]]
          val futurePersistedRoot = repoPool(typeKey[R]).retrieveOne(assoc).map(_.get)
          transform(futurePersistedRoot)(typeKey[R])
        }
        val rootTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[Root]]
        unpersistedRoot(rootTypeKey).map(Assoc(_).asInstanceOf[B])
      }
  }

}