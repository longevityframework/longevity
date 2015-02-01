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
 * (Code presented here is in TypeKeyMapSpec.scala, up at the top)
 * 
 * @tparam TypeBound the upper bound on the type parameters passed to the TypeKey and Val types
 * @tparam Val the parameterized type of the values in the map
 *
 * @see [[ShorthandPool]] for an example of how to use type key maps when the value type is more
 * sophisticated than just type with a single type parameter.
 * @see TypeKeyMapSpec.scala and BaseTypeBoundMapSpec.scala for many more examples
 */
class TypeKeyMap[TypeBound, Val[_ <: TypeBound]] private (underlying: Map[Any, Any])
extends BaseTypeBoundMap[TypeBound, TypeKey, Val](underlying) {

  /** Retrieves the value which is associated with the given type key
   * @tparam TypeParam the type param binding both the type key and the value
   */
  @throws[NoSuchElementException]("when no value is mapped to the supplied type param")
  def apply[TypeParam <: TypeBound : TypeKey]: Val[TypeParam] = get[TypeParam].get

  /** Optionally returns the value associated with the given type key
   * @tparam TypeParam the type param bounding both the type key and the value
   * @return an option value containing the value associated with type key in this map, or None if none
   * exists. */
  def get[TypeParam <: TypeBound : TypeKey]: Option[Val[TypeParam]] =
    underlying.get(typeKey[TypeParam]).asInstanceOf[Option[Val[TypeParam]]]

  /** Returns the value associated with a type key, or a default value if the type key is not contained in the
   * map.
   *
   * @param default a computation that yields a default value in case no binding for the type key is found in
   * the map
   * @tparam TypeParam the type param bounding both the type key and the value
   * @return the value associated with type key if it exists, otherwise the result of the `default` computation.
   */
  def getOrElse[TypeParam <: TypeBound : TypeKey](default: => Val[TypeParam]): Val[TypeParam] =
    underlying.getOrElse(typeKey[TypeParam], default).asInstanceOf[Val[TypeParam]]

  /** Adds a typekey/value pair to this map, returning a new map.
   * @param pair the typekey/value pair
   * @param valConforms a constraint ensuring that `Val[ValTypeParam] <: Val[TypeParam])`
   * @tparam TypeParam the type param bounding both the type key and the value
   * @tparam ValTypeParam the type param for the value type. this can be any type, provided that
   * `Val[ValTypeParam] <: Val[TypeParam])`
   */
  def +[
    TypeParam <: TypeBound,
    ValTypeParam <: TypeBound](
    pair: (TypeKey[TypeParam], Val[ValTypeParam]))(
    implicit valConforms: Val[ValTypeParam] <:< Val[TypeParam])
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](underlying + pair)

  /** Adds a typekey/value pair to this map, returning a new map. The type key is inferred from the type of
   * the supplied value.
   *
   * PLEASE NOTE: Using this method when your `Val` type is contravariant in its type parameter will not
   * do what you might expect! When the compiler infers type parameter `[TypeParam <: TypeBound]` from an
   * argument of type `Contra[TypeParam]`, where type `Contra` is defined as, e.g., `trait Contra[+T]`,
   * it's always going to infer `TypeBound` as the `TypeParam`. There seems to be nothing i can do within
   * `TypeKeyMap` to circumvent this. THe easiest way to work around this problem is to specify the type key
   * yourself with [[TypeKeyMap.+[TypeParam<:TypeBound,ValTypeParam<:TypeBound]* the alternate method +]].
   *
   * @param value the value to add to the map
   * @param key the type key, which is inferred from the type of value
   * @tparam TypeParam the type param
   */
  def +[
    TypeParam <: TypeBound](
    value: Val[TypeParam])(
    implicit
    key: TypeKey[TypeParam])
  : TypeKeyMap[TypeBound, Val] =
    new TypeKeyMap[TypeBound, Val](underlying + (key -> value))

  /** Transforms this map by applying a function to every retrieved value.
   * @param f the function used to transform values of this map.
   * @return a map view which maps every key of this map to f(this(key)).
   */
  def mapValues[
    Val2[_ <: TypeBound]](
    f: TypeBoundFunction[TypeBound, Val, Val2])
  : TypeKeyMap[TypeBound, Val2] = {
    def mapValue[TypeParam <: TypeBound](value1: Val[TypeParam]): Val2[TypeParam] = {
      f.apply[TypeParam](value1)
    }
    val newUnderlying = underlying.mapValues { value1 =>
      mapValue(value1.asInstanceOf[Val[_ <: TypeBound]])
    }
    new TypeKeyMap[TypeBound, Val2](newUnderlying)
  }

  override def hashCode = underlying.hashCode

  /** Compares two maps structurally; i.e., checks if all mappings contained in this map are also contained in
   * the other map, and vice versa.
   * @param that the other type key map
   * @return true if both maps contain exactly the same mappings, false otherwise.
   */
  override def equals(that: Any) = {
    that.isInstanceOf[TypeKeyMap[TypeBound, Val]] &&
    that.asInstanceOf[TypeKeyMap[TypeBound, Val]].underlying == underlying
  }

  /** A string representation of a TypeKeyMap */
  override def toString = s"TypeKey${underlying}"

}
