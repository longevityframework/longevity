package emblem

import scala.language.higherKinds
import scala.reflect.runtime.universe._

// TODO this extravagence is not used anywhere!

object TypeTagMap {

  def empty[V[_]] = apply[V]()

  /** creates a new, empty type tag map */
  def apply[V[_]](): TypeTagMap[V] = new TypeTagMap[V](Map())

  /** creates a new type tag map with a single entry */
  def apply[K : TypeTag, V[K]](value: V[K]): TypeTagMap[V] = TypeTagMap() + (typeTag[K] -> value)

  /** creates a new type tag map with a single entry */
  def apply[K, V[K]](pair: (TypeTag[K], V[K])): TypeTagMap[V] = TypeTagMap() + pair

  /** creates a new type tag map with two entries */
  def apply[K, K1 <: K : TypeTag, K2 <: K : TypeTag, V[K]](v1: V[K1], v2: V[K2]): TypeTagMap[V] =
    TypeTagMap() + (typeTag[K1] -> v1) + (typeTag[K2] -> v2)

  /** creates a new type tag map with three entries */
  def apply[K, K1 <: K : TypeTag, K2 <: K : TypeTag, K3 <: K : TypeTag, V[K]](
    v1: V[K1], v2: V[K2], v3: V[K3]): TypeTagMap[V] =
    TypeTagMap() + (typeTag[K1] -> v1) + (typeTag[K2] -> v2) + (typeTag[K3] -> v3)

}

/** TODO scaladoc */
class TypeTagMap[+V[_]] private (
  private val typeTagToValue: Map[TypeTag[_], _]
) {

  def get[K](implicit typeTag: TypeTag[K]): Option[V[K]] =
    typeTagToValue.get(typeTag).asInstanceOf[Option[V[K]]]

  def apply[K](implicit typeTag: TypeTag[K]): V[K] = get(typeTag).get

  def +[K : TypeTag, V2[K] >: V[K]](value: V2[K]): TypeTagMap[V2] = this + (typeTag[K], value)

  def +[K, V2[K] >: V[K]](pair: (TypeTag[K], V2[K])): TypeTagMap[V2] = {
    new TypeTagMap[V2](typeTagToValue + pair)
  }

  def isEmpty = typeTagToValue.isEmpty

  def size = typeTagToValue.size // TODO tests

}
