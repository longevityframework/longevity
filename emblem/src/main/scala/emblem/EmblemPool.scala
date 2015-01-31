package emblem

import emblem.exceptions.DuplicateEmblemsException

object EmblemPool {

  /** Collects a sequence of [[Emblem emblems]] into a [[EmblemPool]].
   * @param emblems the sequence of emblems stored in the pool */
  @throws[DuplicateEmblemsException]("when two Emblems have the same Long type")
  def apply(emblems: Emblem[_ <: HasEmblem]*): EmblemPool = {

    val map: TypeKeyMap[HasEmblem, Emblem] =
      emblems.foldLeft(TypeKeyMap[HasEmblem, Emblem]()) {
        case (map, emblem) => map + (emblem.typeKey -> emblem)
      }

    if (emblems.size != map.size) throw new DuplicateEmblemsException

    map
  }

}
