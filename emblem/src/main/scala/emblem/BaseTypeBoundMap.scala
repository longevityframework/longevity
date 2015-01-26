package emblem

import scala.language.higherKinds

// TODO put in all the map api
// TODO scaladoc for methods here

/** an abstract base class for shared functionality of [[TypeKeyMap]] and [[TypeBoundMap]] */
private[emblem] abstract class BaseTypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] (
  protected val map: Map[Any, Any]) {

  def keys: Iterable[Key[_ <: TypeBound]] = map.keys.asInstanceOf[Iterable[Key[_ <: TypeBound]]]

  def size = map.size

  def isEmpty = map.isEmpty

}
