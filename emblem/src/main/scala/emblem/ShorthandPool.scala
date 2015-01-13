package emblem

/** a glorified sequence of [[Shorthand shorthands]] that provides for shorthand lookup by type key.
 * we only provide lookup by the longhand type, since it is epected that multiple longhand types will
 * map to the same shorthand type.
 *
 * A shorthand pool requires that no two shorthands have the same Long type.
 *
 * @param shorthands the sequence of shorthands stored in the pool
 * @throws ShorthandPool.DuplicateShorthandsException when two Shorthands have the same Long type
 */
case class ShorthandPool(val shorthands: Shorthand[_, _]*) {

  private val longTypeKeyMap: Map[TypeKey[_], Shorthand[_, _]] = {
    val map: Map[TypeKey[_], Shorthand[_, _]] = shorthands.map(s => (s.longTypeKey -> s)).toMap
    if (shorthands.size != map.size) throw new ShorthandPool.DuplicateShorthandsException
    map
  }

  /** retrieves an optional [[Shorthand]] for the specified Long type. returns `None` if the Long type
   * is not represented in the pool. */
  def longTypeKeyToShorthand[Long](key: TypeKey[Long]): Option[Shorthand[Long, _]] =
    longTypeKeyMap.get(key).asInstanceOf[Option[Shorthand[Long, _]]]

}

object ShorthandPool {

  class DuplicateShorthandsException
  extends Exception("a ShorthandPool cannot contain multiple Shorthands with the same Long type")

}
