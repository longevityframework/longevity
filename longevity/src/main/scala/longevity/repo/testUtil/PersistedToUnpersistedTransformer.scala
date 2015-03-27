package longevity.repo.testUtil

import emblem._
import emblem.traversors.Transformer
import emblem.traversors.Transformer.CustomTransformer
import emblem.traversors.Transformer.emptyCustomTransformers
import longevity.context.LongevityContext
import longevity.domain.Assoc
import longevity.domain.AssocAny
import longevity.domain.RootEntity
import longevity.domain.UnpersistedAssoc
import longevity.exceptions.AssocIsUnpersistedException
import longevity.repo.PersistedAssoc

/** traverses an entity graph, replacing every [[longevity.repo.PersistedAssoc persisted assoc]] with an
 * [[longevity.domain.UnpersistedAssoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 */
class PersistedToUnpersistedTransformer(private val longevityContext: LongevityContext)
extends Transformer {

  override protected val emblemPool = longevityContext.subdomain.entityEmblemPool
  override protected val shorthandPool = longevityContext.shorthandPool
  override protected val customTransformers = emptyCustomTransformers + transformAssoc

  private lazy val transformAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: B): B = input match {
      case unpersistedAssoc: UnpersistedAssoc[_] =>
        throw new AssocIsUnpersistedException(unpersistedAssoc)
      case persistedAssoc: PersistedAssoc[_] => {
        val persistedEntity = input.persisted
        val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[RootEntity]]
        val unpersistedEntity = transform(persistedEntity)(entityTypeKey)
        Assoc(unpersistedEntity).asInstanceOf[B]
      }
    }
  }

}
