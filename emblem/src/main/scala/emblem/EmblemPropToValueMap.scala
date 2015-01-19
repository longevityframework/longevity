package emblem

/** a mapping from [[EmblemProp emblem properties]] to their property values.
 *
 * @tparam T the emblemized type that these [EmblemProp emblem props]] reflect on
 */
class EmblemPropToValueMap[T <: HasEmblem] private(
  private val propToValueMap: Map[EmblemProp[T, _], Any] = Map[EmblemProp[T, _], Any]()) {

  private val propNameToPropMap: Map[String, EmblemProp[T, _]] = propToValueMap.keys.map { prop =>
    (prop.name, prop)
  }.toMap

  /** gets the value for the supplied prop */
  @throws[EmblemPropToValueMap.NoValueForPropName]("when there is no value set for the supplied prop")
  def get[U](prop: EmblemProp[T, U]): U = propToValueMap.get(prop) match {
    case Some(u) => u.asInstanceOf[U]
    case None => throw new EmblemPropToValueMap.NoValueForPropName(prop.name, this)
  }

  /** gets an optional value for the supplied prop name */
  private[emblem] def getOptionByName[U](propName: String): Option[U] = {
    val propOption = propNameToPropMap.get(propName).asInstanceOf[Option[EmblemProp[T, U]]]
    propOption map { propToValueMap(_).asInstanceOf[U] }
  }

  /** creates a new map by adding a new property/value pair to this map */
  def +[U](pair: (EmblemProp[T, U], U)): EmblemPropToValueMap[T] =
    new EmblemPropToValueMap(propToValueMap + pair)

  def size = propToValueMap.size

  def isEmpty = propToValueMap.isEmpty
  
}

object EmblemPropToValueMap {

  /** creates a new, empty map */
  def apply[T <: HasEmblem](): EmblemPropToValueMap[T] = new EmblemPropToValueMap[T]()

  /** an exception thrown when looking up a value for a property that is not in the map */
  class NoValueForPropName(val propName: String, val map: EmblemPropToValueMap[_])
  extends Exception(s"no value for prop $propName in $map")

}
