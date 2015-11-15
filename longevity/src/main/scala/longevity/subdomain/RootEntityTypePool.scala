package longevity.subdomain

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateEntityTypesException

/** houses methods for constructing root entity type pools */
object RootEntityTypePool {

  /** extracts all the [[RootEntityType root entity types]] out of the [[EntityTypePool]], collects them
   * into a [[RootEntityTypePool]], and returns that */
  def apply(entityTypePool: EntityTypePool): RootEntityTypePool = {
    val rootEntityTypes = entityTypePool.values.collect {
      case rootEntityType: RootEntityType[_] => rootEntityType
    }
    RootEntityTypePool(rootEntityTypes.toSeq: _*)
  }

  /** collects a sequence of [[RootEntityType root entity types]] into a [[RootEntityTypePool]].
   * @param entityTypes the sequence of entity types stored in the pool */
  @throws[DuplicateEntityTypesException]("when two EntityTypes have the same Entity type")
  def apply(entityTypes: RootEntityType[_ <: RootEntity]*): RootEntityTypePool = {

    val map: TypeKeyMap[RootEntity, RootEntityType] =
      entityTypes.foldLeft(TypeKeyMap[RootEntity, RootEntityType]()) {
        case (map, entityType) => map + (entityType.emblem.typeKey -> entityType)
      }

    if (entityTypes.size != map.size) throw new DuplicateEntityTypesException

    map
  }

}
