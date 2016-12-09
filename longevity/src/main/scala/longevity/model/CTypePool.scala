package longevity.model

import emblem.TypeKeyMap
import longevity.exceptions.model.DuplicateCTypesException

/** contains factory methods for creating a [[CTypePool component type pool]] */
object CTypePool {

  /** Collects a sequence of [[CType component types]] into a [[CTypePool]].
   *
   * @param eTypes the sequence of component types stored in the pool
   *
   * @throws longevity.exceptions.model.DuplicateCTypesException when two
   * `CTypes` refer to the same component type
   */
  @throws[DuplicateCTypesException]("when two CTypes refer to the same component type")
  def apply(cTypes: CType[_]*): CTypePool = {
    val map: TypeKeyMap[Any, CType] = cTypes.foldLeft(TypeKeyMap[Any, CType]()) {
      case (map, cType) => map + (cType.cTypeKey -> cType)
    }
    if (cTypes.size != map.size) throw new DuplicateCTypesException
    map
  }

  /** an empty component type pool */
  val empty: CTypePool = apply()

}
