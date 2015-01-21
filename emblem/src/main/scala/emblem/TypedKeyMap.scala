package emblem

import scala.language.higherKinds

// TODO scaladoc
// TODO specs
// TODO expand usage through emblem
// TODO decorator classes for more complex K/V types
// TODO put in all the map api

// TypedMap
// this one if you just want to instantiate
class RenameMe[
  TK,
  K[_ <: TK],
  V[_ <: TK]](map: Map[Any, Any]) extends TypedKeyMap[TK, K, V, RenameMe[TK, K, V]](map) {

  protected def newInstance(map: Map[Any, Any]) = new RenameMe[TK, K, V](map)

}

// BaseTypedMap
// this one if you want to subclass.
abstract class TypedKeyMap[
  TK,
  K[_ <: TK],
  V[_ <: TK],
  TKM <: TypedKeyMap[TK, K, V, _]] (
  protected val map: Map[Any, Any]) {

  protected def newInstance(map: Map[Any, Any]): TKM

  def get[TK1 <: TK : TypeKey](key: K[TK1]): Option[V[TK1]] =
    map.get(key).asInstanceOf[Option[V[TK1]]]

  def +[TK1 <: TK : TypeKey](pair: (K[TK1], V[TK1])): TKM =
    newInstance(map + pair)

  def keys: Iterable[K[_ <: TK]] = map.keys.asInstanceOf[Iterable[K[_ <: TK]]]

  def size = map.size

  def isEmpty = map.isEmpty

}
