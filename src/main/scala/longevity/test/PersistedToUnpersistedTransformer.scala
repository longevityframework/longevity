package longevity.test

import emblem.Emblem
import emblem.EmblemProp
import emblem.Emblematic
import emblem.TypeKey
import emblem.traversors.async.Transformer
import emblem.traversors.async.Transformer.CustomTransformer
import emblem.traversors.async.Transformer.CustomTransformerPool
import emblem.typeKey
import longevity.persistence.RepoPool
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** traverses an entity graph, replacing every
 * [[longevity.persistence.PersistedAssoc persisted assoc]] with an
 * [[longevity.subdomain.UnpersistedAssoc unpersisted assoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into
 * its unpersisted equivalent.
 *
 * @param repoPool the repo pool to look up associations with
 * @param executionContext the execution context in which to run
 * @param emblemPool a pool of emblems for the entities to be transformed
 * @param extractorPool a complete set of the extractors used by the bounded context
 */
private[test] class PersistedToUnpersistedTransformer(
  private val repoPool: RepoPool,
  override protected implicit val executionContext: ExecutionContext,
  override protected val emblematic: Emblematic)
extends Transformer {

  override protected val customTransformers = CustomTransformerPool.empty + transformAssoc

  private lazy val transformAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: Future[B]): Future[B] =
      input.flatMap { b =>
        def unpersistedP[P <: Persistent : TypeKey]: Future[P] = {
          val assoc = b.asInstanceOf[Assoc[P]]
          val futurePersistedRoot = repoPool(typeKey[P]).retrieveOne(assoc).map(_.get)
          transform(futurePersistedRoot)(typeKey[P])
        }
        val pTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[Persistent]]
        unpersistedP(pTypeKey).map(Assoc(_).asInstanceOf[B])
      }
  }

}
