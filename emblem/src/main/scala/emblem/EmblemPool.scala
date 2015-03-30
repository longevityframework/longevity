package emblem

import emblem.exceptions.DuplicateEmblemsException

object EmblemPool {

  /** Collects a sequence of [[Emblem emblems]] into a [[EmblemPool]].
   * @param emblems the sequence of emblems stored in the pool
   * @throws emblem.exceptions.DuplicateEmblemsException when two or more of the Emblems have the same Long type
   */
  def apply(emblems: Emblem[_ <: HasEmblem]*): EmblemPool = {
    val map: EmblemPool = emblems.foldLeft(TypeKeyMap[HasEmblem, Emblem]()) {
      case (map, emblem) => map + (emblem.typeKey -> emblem)
    }
    if (emblems.size != map.size) throw new DuplicateEmblemsException
    map
  }

  /** an empty emblem pool */
  val empty: EmblemPool = TypeKeyMap[HasEmblem, Emblem]

}
