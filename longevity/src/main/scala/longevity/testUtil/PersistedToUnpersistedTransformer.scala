package longevity.testUtil

import emblem._
import emblem.traversors.Transformer
import emblem.traversors.Transformer.CustomTransformer
import emblem.traversors.Transformer.emptyCustomTransformers
import longevity.domain.Assoc
import longevity.domain.BoundedContext
import longevity.domain.RootEntity
import longevity.repo.Id
import longevity.domain.UnpersistedAssoc
import PersistedToUnpersistedTransformer.AssocAny

object PersistedToUnpersistedTransformer {
  private type AssocAny = Assoc[_ <: RootEntity]
}

/** traverses an entity graph, replacing every [[longevity.repo.Id persisted assoc]] with an
 * [[longevity.domain.UnpersistedAssoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 */
class PersistedToUnpersistedTransformer(private val boundedContext: BoundedContext)
extends Transformer {

  override protected val shorthandPool = boundedContext.shorthandPool
  override protected val emblemPool = boundedContext.entityEmblemPool
  override protected val customTransformers = emptyCustomTransformers + transformAssoc

  private lazy val transformAssoc = new CustomTransformer[AssocAny] {
    def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: B): B = input match {
      case unpersistedAssoc: UnpersistedAssoc[_] =>
        throw new EncounteredUnpersistedAssocException(unpersistedAssoc)
      case id: Id[_] => {
        val persistedEntity = input.persisted
        val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[RootEntity]]
        val unpersistedEntity = transform(persistedEntity)(entityTypeKey)
        Assoc(unpersistedEntity).asInstanceOf[B] // TODO see if you get rid of this cast
      }
    }
  }

}
