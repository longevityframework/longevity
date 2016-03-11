package longevity.subdomain

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateEntityTypesException

/** houses methods for constructing persistent type pools */
object PTypePool {

  /** extracts all the [[RootType root types]] out of the [[EntityTypePool]], collects them
   * into a [[PTypePool]], and returns that */
  def apply(entityTypePool: EntityTypePool): PTypePool = {
    val pTypes = entityTypePool.values.collect {
      case pType: PType[_] => pType
    }
    PTypePool(pTypes.toSeq: _*)
  }

  /** collects a sequence of [[PType persistent types]] into a [[PTypePool]].
   * @param entityTypes the sequence of entity types stored in the pool
   */
  @throws[DuplicateEntityTypesException]("when two EntityTypes have the same Entity type")
  def apply(entityTypes: PType[_ <: Persistent]*): PTypePool = {

    val map: TypeKeyMap[Persistent, PType] =
      entityTypes.foldLeft(TypeKeyMap[Persistent, PType]()) {
        case (map, entityType) => map + (entityType.emblem.typeKey -> entityType)
      }

    if (entityTypes.size != map.size) throw new DuplicateEntityTypesException

    map
  }

}
