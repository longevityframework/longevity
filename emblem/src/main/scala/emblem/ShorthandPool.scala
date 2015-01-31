package emblem

import emblem.exceptions.DuplicateShorthandsException

object ShorthandPool {

  /** Collects a sequence of [[Shorthand shorthands]] into a [[ShorthandPool]].
   * @param shorthands the sequence of shorthands stored in the pool */
  @throws[DuplicateShorthandsException]("when two Shorthands have the same Long type")
  def apply(shorthands: Shorthand[_, _]*): ShorthandPool = {

    val longTypeKeyMap: TypeKeyMap[Any, ShorthandFor] =
      shorthands.foldLeft(TypeKeyMap[Any, ShorthandFor]()) {
        case (map, shorthand) => map + (shorthand.longTypeKey, shorthand)
      }

    if (shorthands.size != longTypeKeyMap.size) throw new DuplicateShorthandsException

    longTypeKeyMap
  }

}
