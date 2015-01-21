package emblem

import scala.language.higherKinds

// TODO scaladoc
// TODO specs
// TODO put in all the map api

object TypedMap {

  /** Creates and returns an empty [[TypedMap]] for the supplied types
   * @tparam TypeBound TODO
   * @tparam Key TODO
   * @tparam Val TODO
   */
  def apply[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]](): TypedMap[TypeBound, Key, Val] =
    new TypedMap[TypeBound, Key, Val](Map.empty)

}

/**
 * 
 * @see ShorthandPool for an example of how to use typed maps when the key/value types are more
 * sophisticated than just type with a single type parameter.
 */
class TypedMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] private (private val map: Map[Any, Any]) {

  @throws[NoSuchElementException]
  def apply[TypeParam <: TypeBound : TypeKey](key: Key[TypeParam]): Val[TypeParam] = get(key).get

  def get[TypeParam <: TypeBound : TypeKey](key: Key[TypeParam]): Option[Val[TypeParam]] =
    map.get(key).asInstanceOf[Option[Val[TypeParam]]]

  // we need an explicit type key here, or the compiler will just infer TypeParam =:= TypeBound!
  def +[
    TypeParam <: TypeBound : TypeKey](
    keyedPair: ((TypeKey[TypeParam], Key[TypeParam]), Val[TypeParam]))
  : TypedMap[TypeBound, Key, Val] =
    new TypedMap[TypeBound, Key, Val](map + (keyedPair._1._2 -> keyedPair._2))

  def keys: Iterable[Key[_ <: TypeBound]] = map.keys.asInstanceOf[Iterable[Key[_ <: TypeBound]]]

  def size = map.size

  def isEmpty = map.isEmpty

}
