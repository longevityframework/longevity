package emblem

import scala.language.higherKinds

object TypeKeyMap {

  /** Creates and returns an empty [[TypeKeyMap]] for the supplied types
   * @tparam TypeBound the upper bound on the type parameters passed to the TypeKey and Val types
   * @tparam Val the parameterized type of the values in the map
   */
  def apply[TypeBound, Val[_ <: TypeBound]](): TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](Map.empty)

}

/** A map where the keys are [[TypeKey TypeKeys]] with an upper bound, and the values have a type parameter
 * with the same bound. The key and value of each key/value pair are constrained to match on that type
 * parameter. For example, suppose we are maintaining an inventory of computer parts:
 *
 * {{{
 * sealed trait ComputerPart
 * case class Memory(gb: Int) extends ComputerPart
 * case class CPU(mhz: Double) extends ComputerPart
 * case class Display(resolution: Int) extends ComputerPart
 * }}}
 * 
 * We can use a `TypeKeyMap` to store a list of parts for each kind of part:
 *
 * {{{
 * var partLists = TypeKeyMap[ComputerPart, List]()
 * partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
 * partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
 * partLists += Display(720) :: Display(1080) :: Nil
 * }}}
 *
 * Now we can look up part lists by part type, with everything coming back as the expected type:
 * 
 * {{{
 * val memories: List[Memory] = partLists[Memory]
 * memories.size should be (3)
 * val cpus: List[CPU] = partLists[CPU]
 * cpus.size should be (3)
 * val displays: List[Display] = partLists[Display]
 * displays.size should be (2)
 *
 * val cpu: CPU = partLists[CPU].head
 * cpu should equal (CPU(2.2))
 * val display: Display = partLists[Display].tail.head
 * display should equal (Display(1080))
 * }}}
 *
 * Note that the API does not provide `++` or similar methods to add multiple key/value pairs at a time, as
 * each pair needs to be type-checked separately.
 *
 * (Code presented here is in TypeKeyMapSpec.scala)
 * 
 * @tparam TypeBound the upper bound on the type parameters passed to the TypeKey and Val types
 * @tparam Val the parameterized type of the values in the map
 *
 * @see [[ShorthandPool]] for an example of how to use type key maps when the value type is more
 * sophisticated than just type with a single type parameter.
 * @see TypeKeyMapSpec.scala and BaseTypedMapSpec.scala for many more examples
 */
class TypeKeyMap[TypeBound, Val[_ <: TypeBound]] private (map: Map[Any, Any])
extends BaseTypedMap[TypeBound, TypeKey, Val](map) {

  // TODO scaladoc

  @throws[NoSuchElementException]
  def apply[TypeParam <: TypeBound : TypeKey]: Val[TypeParam] = get[TypeParam].get

  def get[TypeParam <: TypeBound : TypeKey]: Option[Val[TypeParam]] =
    map.get(typeKey[TypeParam]).asInstanceOf[Option[Val[TypeParam]]]

  def getOrElse[TypeParam <: TypeBound : TypeKey](default: => Val[TypeParam]): Val[TypeParam] =
    map.getOrElse(typeKey[TypeParam], default).asInstanceOf[Val[TypeParam]]

  def +[
    TypeParam <: TypeBound,
    KeyTypeParam <: TypeBound,
    ValTypeParam <: TypeBound](
    pair: (TypeKey[KeyTypeParam], Val[ValTypeParam]))(
    implicit
    keyStrictly: KeyTypeParam =:= TypeParam,
    valLoosely: Val[ValTypeParam] <:< Val[TypeParam])
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](map + pair)

  def +[
    TypeParam <: TypeBound](
    value: Val[TypeParam])(
    implicit
    key: TypeKey[TypeParam])
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](map + (key -> value))

  override def toString = s"TypeKey${map}"

}
