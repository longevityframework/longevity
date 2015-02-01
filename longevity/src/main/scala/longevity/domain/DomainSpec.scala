package longevity.domain

import emblem._

// TODO rename DomainConfig?
/** a specification of a project's domain. contains a pool of all the [[EntityType entity types]] in the
 * domain, as well as all the [[Shorthand shorthands]] used by the entities. */
case class DomainSpec(
  entityTypePool: EntityTypePool,
  shorthandPool: ShorthandPool) {

  val entityEmblemPool: TypeKeyMap[Entity, Emblem] = {

    val toEmblem = new TypeBoundFunction[Entity, EntityType, Emblem] {
      def apply[E <: Entity](entityType: EntityType[E]): Emblem[E] = entityType.emblem
    }

    entityTypePool.mapValues[Emblem](toEmblem)
  }

}
