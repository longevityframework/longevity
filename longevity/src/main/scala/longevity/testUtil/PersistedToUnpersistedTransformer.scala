package longevity.testUtil

import emblem._
import emblem.traversors.Transformer
import emblem.traversors.Transformer.CustomTransformer
import emblem.traversors.Transformer.emptyCustomTransformers
import longevity.domain.Assoc
import longevity.domain.DomainConfig
import longevity.domain.Entity
import longevity.repo.Id
import longevity.domain.UnpersistedAssoc
import PersistedToUnpersistedTransformer.AssocAny

object PersistedToUnpersistedTransformer {

  private type AssocAny = Assoc[_ <: Entity]

}

/** traverses an entity graph, replacing every [[Id persisted assoc]] with an [[UnpersistedAssoc]].
 *
 * this is useful for testing purposes, as it transforms a persisted entity into its unpersisted equivalent.
 */
class PersistedToUnpersistedTransformer(private val domainConfig: DomainConfig) {

  /** replaces every [[Id persisted assoc]] with an [[UnpersistedAssoc]].
   * 
   * @throws EncounteredUnpersistedAssocException whenever an unpersisted assoc is encountered in the input.
   * this usually indicates an error in persisting the entity that prevents the entity from being fully
   * persisted.
   */
  def transform[A : TypeKey](input: A): A = transformer.transform[A](input)

  val transformer = new Transformer {

    override protected def shorthandPool = domainConfig.shorthandPool
    override protected def emblemPool = domainConfig.entityEmblemPool

    private lazy val transformAssoc = new CustomTransformer[AssocAny] {
      def apply[B <: AssocAny : TypeKey](transformer: Transformer, input: B): B = input match {
        case unpersistedAssoc: UnpersistedAssoc[_] =>
          throw new EncounteredUnpersistedAssocException(unpersistedAssoc)
        case id: Id[_] => {
          val persistedEntity = input.persisted
          val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[Entity]]
          val unpersistedEntity = transform(persistedEntity)(entityTypeKey)
          Assoc(unpersistedEntity).asInstanceOf[B] // TODO get rid of this cast
        }
      }
    }

    override protected def customTransformers = emptyCustomTransformers + transformAssoc

  }

}
