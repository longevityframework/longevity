package longevity.subdomain.ptype

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateEntityTypesException
import longevity.subdomain.EntityTypePool
import longevity.subdomain.persistent.Persistent

/** houses methods for constructing persistent type pools */
object PTypePool {

  /** extracts all the [[PType persistent types]] out of an [[EntityTypePool]],
   * collects them into a [[PTypePool]], and returns that
   */
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
        case (map, entityType) => map + (entityType.entityTypeKey -> entityType)
      }

    if (entityTypes.size != map.size) throw new DuplicateEntityTypesException

    map
  }

}
