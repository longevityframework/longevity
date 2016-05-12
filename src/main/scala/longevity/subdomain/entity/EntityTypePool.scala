package longevity.subdomain.entity

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateEntityTypesException

object EntityTypePool {

  /** Collects a sequence of [[EntityType entity types]] into a [[EntityTypePool]].
   * @param entityTypes the sequence of entity types stored in the pool */
  @throws[DuplicateEntityTypesException]("when two EntityTypes have the same Entity type")
  def apply(entityTypes: EntityType[_ <: Entity]*): EntityTypePool = {

    val map: TypeKeyMap[Entity, EntityType] =
      entityTypes.foldLeft(TypeKeyMap[Entity, EntityType]()) {
        case (map, entityType) => map + (entityType.entityTypeKey -> entityType)
      }

    if (entityTypes.size != map.size) throw new DuplicateEntityTypesException

    map
  }

  /** an empty entity type pool */
  val empty: EntityTypePool = apply()

}
