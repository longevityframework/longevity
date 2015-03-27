package longevity.persistence.testUtil

import emblem._
import emblem.traversors.Transformer
import emblem.traversors.Transformer.CustomTransformer
import emblem.traversors.Transformer.emptyCustomTransformers
import longevity.context.LongevityContext
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.RootEntity
import longevity.subdomain.UnpersistedAssoc
import longevity.exceptions.AssocIsUnpersistedException
import longevity.persistence.PersistedAssoc

/** traverses an entity graph, replacing every [[longevity.persistence.PersistedAssoc persisted assoc]] with an
 * [[longevity.subdomain.UnpersistedAssoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 */
class PersistedToUnpersistedTransformer(private val longevityContext: LongevityContext)
extends Transformer {

  override protected val emblemPool = longevityContext.entityEmblemPool
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
