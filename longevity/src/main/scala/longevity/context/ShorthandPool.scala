package longevity.context

import emblem.imports._
import longevity.exceptions.DuplicateShorthandsException

object ShorthandPool {

  /** Collects a sequence of [[Shorthand shorthands]] into a [[ShorthandPool]].
   * @param shorthands the sequence of shorthands stored in the pool
   * @throws longevity.exceptions.DuplicateShorthandsException when two or more of the shorthands have the
   * same Actual type
   */
  def apply(shorthands: Shorthand[_, _]*): ShorthandPool = {
    val actualTypeKeyMap: ShorthandPool = shorthands.foldLeft(TypeKeyMap[Any, ShorthandFor]()) {
      case (map, shorthand) => map + (shorthand.actualTypeKey -> shorthand)
    }
    if (shorthands.size != actualTypeKeyMap.size) throw new DuplicateShorthandsException
    actualTypeKeyMap
  }

  /** an empty shorthand pool */
  val empty = ShorthandPool()

}
