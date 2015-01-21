package emblem

import scala.language.higherKinds

// TODO scaladoc
// TODO specs
// TODO put in all the map api

object TypedMap {

  /** Creates and returns an empty [[TypedMap]] for the supplied types
   * @tparam TK TODO
   * @tparam K TODO
   * @tparam V TODO
   */
  def apply[TK, K[_ <: TK], V[_ <: TK]](): TypedMap[TK, K, V] = new TypedMap[TK, K, V](Map.empty)

}

/**
 * 
 * @see ShorthandPool for an example of how to use typed maps when the key/value types are more
 * sophisticated than just type with a single type parameter.
 */
class TypedMap[TK, K[_ <: TK], V[_ <: TK]] private (private val map: Map[Any, Any]) {

  def get[TK1 <: TK : TypeKey](key: K[TK1]): Option[V[TK1]] =
    map.get(key).asInstanceOf[Option[V[TK1]]]

  def +[TK1 <: TK : TypeKey](pair: (K[TK1], V[TK1])): TypedMap[TK, K, V] = new TypedMap[TK, K, V](map + pair)

  def keys: Iterable[K[_ <: TK]] = map.keys.asInstanceOf[Iterable[K[_ <: TK]]]

  def size = map.size

  def isEmpty = map.isEmpty

}
