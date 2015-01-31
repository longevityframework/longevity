package emblem

import scala.language.higherKinds

// TODO put in more of the map api

/** an abstract base class for shared functionality of [[TypeKeyMap]] and [[TypeBoundMap]] */
private[emblem] abstract class BaseTypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] (
  protected val map: Map[Any, Any]) {

  /** Tests whether the map is empty.
   * @return `true` if the map does not contain any key/value binding, `false` otherwise.
   */
  def isEmpty = map.isEmpty

  /** Collects all keys of this map in an iterable collection.
   * @return the keys of this map as an iterable.
   */
  def keys: Iterable[Key[_ <: TypeBound]] = map.keys.asInstanceOf[Iterable[Key[_ <: TypeBound]]]

  /** The number of key/value bindings in this map */
  def size = map.size

  /** Collects all values of this map in an iterable collection.
   * @return the values of this map as an iterable. */
  def values: collection.Iterable[Val[_ <: TypeBound]] = map.values.asInstanceOf[Iterable[Val[_ <: TypeBound]]]

}
