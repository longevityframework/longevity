package longevity.subdomain

import emblem.TypeKeyMap
import longevity.exceptions.subdomain.DuplicateETypesException

/** contains a factory methods for creating a [[ETypePool component type pool]]
 */
object ETypePool {

  /** Collects a sequence of [[EType component types]] into a [[ETypePool]].
   * @param eTypes the sequence of component types stored in the pool
   * @throws longevity.exceptions.subdomain.DuplicateETypesException when two
   * `ETypes` refer to the same component type
   */
  @throws[DuplicateETypesException]("when two ETypes refer to the same component type")
  def apply(eTypes: EType[_]*): ETypePool = {
    val map: TypeKeyMap[Any, EType] = eTypes.foldLeft(TypeKeyMap[Any, EType]()) {
      case (map, eType) => map + (eType.eTypeKey -> eType)
    }
    if (eTypes.size != map.size) throw new DuplicateETypesException
    map
  }

  /** an empty embeddable type pool */
  val empty: ETypePool = apply()

}
