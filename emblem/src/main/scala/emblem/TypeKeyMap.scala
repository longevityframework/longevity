package emblem

import scala.language.higherKinds

// TODO scaladoc
// TODO specs

object TypeKeyMap {

  /** Creates and returns an empty [[TypeKeyMap]] for the supplied types
   * @tparam TypeBound TODO
   * @tparam Val TODO
   */
  def apply[TypeBound, Val[_ <: TypeBound]](): TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](Map.empty)

}

/**
 *
 * @see [[ShorthandPool]] for an example of how to use type key maps when the value type is more
 * sophisticated than just type with a single type parameter.
 * @see TypeKeyMapSpec.scala and BaseTypedMapSpec.scala for many more examples
 */
class TypeKeyMap[TypeBound, Val[_ <: TypeBound]] private (map: Map[Any, Any])
extends BaseTypedMap[TypeBound, TypeKey, Val](map) {

  @throws[NoSuchElementException]
  def apply[TypeParam <: TypeBound : TypeKey]: Val[TypeParam] = get[TypeParam].get

  def get[TypeParam <: TypeBound : TypeKey]: Option[Val[TypeParam]] =
    map.get(typeKey[TypeParam]).asInstanceOf[Option[Val[TypeParam]]]

  def getOrElse[TypeParam <: TypeBound : TypeKey](default: => Val[TypeParam]): Val[TypeParam] =
    map.getOrElse(typeKey[TypeParam], default).asInstanceOf[Val[TypeParam]]

  // we need an explicit type key here, or the compiler will just infer TypeParam =:= TypeBound!
  def +[TypeParam <: TypeBound : TypeKey](pair: (TypeKey[TypeParam], Val[TypeParam]))
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](map + pair)

  // we need an explicit type key here, or the compiler will just infer TypeParam =:= TypeBound!
  def +[TypeParam <: TypeBound : TypeKey](key: TypeKey[TypeParam], value: Val[TypeParam])
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](map + (key -> value))

  override def toString = s"TypeKey${map}"

}
