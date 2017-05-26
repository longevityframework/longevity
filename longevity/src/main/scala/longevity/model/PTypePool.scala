package longevity.model

import emblem.TypeKey
import emblem.TypeKeyMap
import longevity.exceptions.model.DuplicatePTypesException

/** a collection of persistent types for a model, indexed by the persistent evidences
 *
 * @tparam M the model
 * @see PType
 */
abstract class PTypePool[M] {

  /** a persistent type where the model is fixed */
  type PTypeM[P] = PType[M, P]

  private[longevity] val typeKeyMap: TypeKeyMap[Any, PTypeM]

  /** retrieves a persistent type from the pool by its evidence */
  def apply[P](implicit ev: TypeKey[P]): PTypeM[P] = typeKeyMap.apply(ev)

  /** all the persistent types in the pool */
  def values = typeKeyMap.values

  /** the number of persistent types in the pool */
  def size = typeKeyMap.size

}

/** houses methods for constructing persistent type pools */
object PTypePool {

  /** collects a sequence of [[PType persistent types]] into a [[PTypePool]]
   * 
   * @param pTypes the sequence of persistent types stored in the pool
   * @throws longevity.exceptions.model.DuplicatePTypesException when two `PTypes` have the same
   * `Persistent` type
   */
  @throws[DuplicatePTypesException]("when two PTypes have the same Persistent type")
  def apply[M](pTypes: PType[M, _]*): PTypePool[M] = {
    type PTypeM[P] = PType[M, P]
    val map: TypeKeyMap[Any, PTypeM] = pTypes.foldLeft(TypeKeyMap[Any, PTypeM]()) {
      case (map, pType) => map + (pType.pTypeKey -> pType)
    }
    if (pTypes.size != map.size) throw new DuplicatePTypesException
    new PTypePool[M] {
      private[longevity] val typeKeyMap = map
    }
  }

  /** an empty persistent type pool */
  def empty[M]: PTypePool[M] = apply()

}
