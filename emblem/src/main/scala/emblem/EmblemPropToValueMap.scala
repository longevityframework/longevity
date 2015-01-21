package emblem

/** a mapping from [[EmblemProp emblem properties]] to their property values.
 *
 * @tparam T the emblemized type that these [EmblemProp emblem props]] reflect on
 */
class EmblemPropToValueMap[T <: HasEmblem] private (map: Map[Any, Any] = Map())
extends TypedKeyMap[
  T,
  EmblemPropToValueMap.AnyProp,
  Any,
  EmblemPropToValueMap[T]](
  map) {

  protected def newInstance(map: Map[Any, Any]) = new EmblemPropToValueMap[T](map)

  private val propNameToPropMap: Map[String, EmblemProp[_ <: HasEmblem, _]] =
    keys.map { prop: EmblemProp[_ <: HasEmblem, _] =>
      (prop.name, prop)
    }.toMap

  // TODO: this has to be renamed! get should return Option
  /** gets the value for the supplied prop */
  @throws[EmblemPropToValueMap.NoValueForPropName]("when there is no value set for the supplied prop")
  def get[U](prop: EmblemProp[T, U]): U = map.get(prop) match {
    case Some(u) => u.asInstanceOf[U]
    case None => throw new EmblemPropToValueMap.NoValueForPropName(prop.name, this)
  }

  /** gets an optional value for the supplied prop name */
  private[emblem] def getOptionByName[U](propName: String): Option[U] = {
    val propOption = propNameToPropMap.get(propName).asInstanceOf[Option[EmblemProp[T, U]]]
    propOption map { map(_).asInstanceOf[U] }
  }
  
}

object EmblemPropToValueMap {

  type AnyProp[T <: HasEmblem] = EmblemProp[T, _]

  /** creates a new, empty map */
  def apply[T <: HasEmblem](): EmblemPropToValueMap[T] = new EmblemPropToValueMap[T]()

  /** an exception thrown when looking up a value for a property that is not in the map */
  class NoValueForPropName(val propName: String, val map: EmblemPropToValueMap[_])
  extends Exception(s"no value for prop $propName in $map")

}
