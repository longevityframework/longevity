package longevity.model

import emblem.TypeKeyMap
import longevity.exceptions.model.DuplicatePTypesException

/** houses methods for constructing persistent type pools */
object PTypePool {

  /** collects a sequence of [[PType persistent types]] into a [[PTypePool]].
   * 
   * @param pTypes the sequence of persistent types stored in the pool
   * @throws longevity.exceptions.model.DuplicatePTypesException when
   * two `PTypes` have the same `Persistent` type
   */
  @throws[DuplicatePTypesException]("when two PTypes have the same Persistent type")
  def apply(pTypes: PType[_]*): PTypePool = {
    val map: TypeKeyMap[Any, PType] = pTypes.foldLeft(TypeKeyMap[Any, PType]()) {
      case (map, pType) => map + (pType.pTypeKey -> pType)
    }
    if (pTypes.size != map.size) throw new DuplicatePTypesException
    map
  }

  /** an empty persistent type pool */
  val empty: PTypePool = apply()

}
