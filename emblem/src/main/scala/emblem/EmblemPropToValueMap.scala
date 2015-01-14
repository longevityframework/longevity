package emblem

/** a mapping from [[EmblemProp emblem properties]] to their property values.
 *
 * @tparam T the emblemized type that these [EmblemProp emblem props]] reflect on
 */
class EmblemPropToValueMap[T <: HasEmblem] private(
  private val map: Map[EmblemProp[T, _], Any] = Map[EmblemProp[T, _], Any]()) {

  /** gets the value for the supplied prop */
  @throws[EmblemPropToValueMap.NoValueForEmblemProp]("when there is no value set for the supplied prop")
  def get[U](prop: EmblemProp[T, U]): U = map.get(prop) match {
    case Some(u) => u.asInstanceOf[U]
    case None => throw new EmblemPropToValueMap.NoValueForEmblemProp(prop, this)
  }

  /** creates a new map by adding a new property/value pair to this map */
  def +[U](pair: (EmblemProp[T, U], U)): EmblemPropToValueMap[T] =
    new EmblemPropToValueMap(map + pair)
  
}

object EmblemPropToValueMap {

  /** creates a new, empty map */
  def apply[T <: HasEmblem](): EmblemPropToValueMap[T] = new EmblemPropToValueMap[T]()

  /** an exception thrown when looking up a value for a property that is not in the map */
  class NoValueForEmblemProp(val prop: EmblemProp[_, _], val map: EmblemPropToValueMap[_])
  extends Exception(s"no value for prop $prop in map")

}
