package longevity.subdomain

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateEntityTypesException

/** houses methods for constructing root type pools */
object RootTypePool {

  /** extracts all the [[RootType root types]] out of the [[EntityTypePool]], collects them
   * into a [[RootTypePool]], and returns that */
  def apply(entityTypePool: EntityTypePool): RootTypePool = {
    val rootTypes = entityTypePool.values.collect {
      case rootType: RootType[_] => rootType
    }
    RootTypePool(rootTypes.toSeq: _*)
  }

  /** collects a sequence of [[RootType root types]] into a [[RootTypePool]].
   * @param entityTypes the sequence of entity types stored in the pool */
  @throws[DuplicateEntityTypesException]("when two EntityTypes have the same Entity type")
  def apply(entityTypes: RootType[_ <: Root]*): RootTypePool = {

    val map: TypeKeyMap[Root, RootType] =
      entityTypes.foldLeft(TypeKeyMap[Root, RootType]()) {
        case (map, entityType) => map + (entityType.emblem.typeKey -> entityType)
      }

    if (entityTypes.size != map.size) throw new DuplicateEntityTypesException

    map
  }

}
