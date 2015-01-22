package emblem

import emblem.exceptions.DuplicateShorthandsException

/** a glorified sequence of [[Shorthand shorthands]] that provides for shorthand lookup by type key.
 * we only provide lookup by the longhand type, since it is epected that multiple longhand types will
 * map to the same shorthand type.
 *
 * A shorthand pool requires that no two shorthands have the same Long type.
 *
 * @param shorthands the sequence of shorthands stored in the pool
 * @throws emblem.exceptions.DuplicateShorthandsException when two Shorthands have the same Long type
 */
case class ShorthandPool(val shorthands: Shorthand[_, _]*) {

  // this type is equivalent to Shorthand[Long, _], except with a single type parameter Long.
  // this allows it to be used as a key in a TypedMap
  private type ShorthandFor[Long] = Shorthand[Long, _]

  private val longTypeKeyMap: TypeKeyMap[Any, ShorthandFor] =
    shorthands.foldLeft(TypeKeyMap[Any, ShorthandFor]()) {
      case (map, shorthand) => map + (shorthand.longTypeKey, shorthand)
    }

  if (shorthands.size != longTypeKeyMap.size) throw new DuplicateShorthandsException

  /** retrieves an optional [[Shorthand]] for the specified Long type. returns `None` if the Long type
   * is not represented in the pool. */
  def longTypeKeyToShorthand[Long](implicit key: TypeKey[Long]): Option[Shorthand[Long, _]] =
    longTypeKeyMap.get(key)

}
