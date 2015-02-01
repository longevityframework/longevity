package emblem

import scala.language.higherKinds

object TypeBoundMap {

  /** Creates and returns an empty [[TypeBoundMap]] for the supplied types.
   * @tparam TypeBound the upper bound on the type parameters passed into the Key and Value types
   * @tparam Key the parameterized type of the keys in the map
   * @tparam Val the parameterized type of the values in the map
   */
  def apply[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]](): TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](Map.empty)

}

/** A map where the types for keys and values share a type parameter with the same bounds. The key and value
 * of each key/value pair are constrained to match on that type parameter. For example, I might have some pet
 * stores that only cater to a single kind of pet:
 *
 * {{{
 * trait Pet
 * case class Cat(name: String) extends Pet
 * case class Dog(name: String) extends Pet
 * class PetStore[P <: Pet]
 *
 * val catStore1 = new PetStore[Cat]
 * val catStore2 = new PetStore[Cat]
 * val dogStore1 = new PetStore[Dog]
 * }}}
 *
 * We can use a `TypeBoundMap` to store a list of pets of the appropriate type for every pet store:
 *
 * {{{
 * var inventories = TypeBoundMap[Pet, PetStore, List]
 * inventories += (catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
 * inventories += (catStore2, Cat("cat21") :: Nil)
 * inventories += (dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)
 * }}}
 *
 * Now we can look up pet lists by pet store, with everything coming back as the expected type:
 * 
 * {{{
 * val cats1: List[Cat] = inventories(catStore1)
 * cats1.size should be (3)
 * val cats2: List[Cat] = inventories(catStore2)
 * cats2.size should be (1)
 * val dogs1: List[Dog] = inventories(dogStore1)
 * dogs1.size should be (2)
 * 
 * val cat: Cat = inventories(catStore1).head
 * cat should equal (Cat("cat11"))
 * val dog: Dog = inventories(dogStore1).head
 * dog should equal (Dog("dog11"))
 * }}}
 *
 * Note that the API does not provide `++` or similar methods to add multiple key/value pairs at a time, as
 * each pair needs to be type-checked separately.
 *
 * (Code presented here is in TypeBoundMapSpec.scala, up at the top)
 * 
 * @tparam TypeBound the upper bound on the type parameters passed to the Key and Val types
 * @tparam Key the parameterized type of the keys in the map
 * @tparam Val the parameterized type of the values in the map
 * 
 * @see TypeBoundMapSpec.scala and BaseTypeBoundMapSpec.scala for many more examples
 */
class TypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] private (underlying: Map[Any, Any])
extends BaseTypeBoundMap[TypeBound, Key, Val](underlying) {

  /** Retrieves the value which is associated with the given key, both bound by the same type param.
   * @tparam TypeParam the type param binding both the key and the value */
  @throws[NoSuchElementException]("when no value is mapped to the supplied key")
  def apply[TypeParam <: TypeBound](key: Key[TypeParam]): Val[TypeParam] = get(key).get

  /** Optionally returns the value associated with the given key
   * @tparam TypeParam the type param bounding both the key and the value
   * @return an option value containing the value associated with type key in this map, or None if none
   * exists.
   */
  def get[TypeParam <: TypeBound](key: Key[TypeParam]): Option[Val[TypeParam]] =
    underlying.get(key).asInstanceOf[Option[Val[TypeParam]]]

  /** Returns the value associated with a key, or a default value if the key is not contained in the map.
   *
   * @param default a computation that yields a default value in case no binding for the key is found in
   * the map
   * @tparam TypeParam the type param bounding both the key and the value
   * @return the value associated with key if it exists, otherwise the result of the `default` computation.
   */
  def getOrElse[TypeParam <: TypeBound](
    key: Key[TypeParam], default: => Val[TypeParam]): Val[TypeParam] =
    underlying.getOrElse(key, default).asInstanceOf[Val[TypeParam]]

  /** Adds a key/value pair to this map, returning a new map. Both the key and the value are bound by the same
   * type param.
   * @param pair the key/value pair
   * @param valConforms a constraint ensuring that `Val[ValTypeParam] <: Val[TypeParam])`
   * @tparam TypeParam the type param bounding both the key and the value
   * @tparam ValTypeParam the type param for the value type. this can be any type, provided that
   * `Val[ValTypeParam] <: Val[KeyTypeParam])`
   */
  def +[
    TypeParam <: TypeBound,
    ValTypeParam <: TypeBound](
    pair: (Key[TypeParam], Val[ValTypeParam]))(
    implicit
    valConforms: Val[ValTypeParam] <:< Val[TypeParam])
  : TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](underlying + pair)

  override def hashCode = underlying.hashCode

  /** Transforms this map by applying a function to every retrieved value.
   * @param f the function used to transform values of this map.
   * @return a map view which maps every key of this map to f(this(key)).
   */
  def mapValues[
    Val2[_ <: TypeBound]](
    f: TypeBoundFunction[TypeBound, Val, Val2])
  : TypeBoundMap[TypeBound, Key, Val2] = {
    def mapValue[TypeParam <: TypeBound](value1: Val[TypeParam]): Val2[TypeParam] = {
      f.apply[TypeParam](value1)
    }
    val newUnderlying = underlying.mapValues { value1 =>
      mapValue(value1.asInstanceOf[Val[_ <: TypeBound]])
    }
    new TypeBoundMap[TypeBound, Key, Val2](newUnderlying)
  }

  /** A string representation of a TypeBoundMap */
  override def toString = s"TypeBound${underlying}"

  /** Compares two maps structurally; i.e., checks if all mappings contained in this map are also contained in
   * the other map, and vice versa.
   * @param that the other type key map
   * @return true if both maps contain exactly the same mappings, false otherwise.
   */
  override def equals(that: Any) =
    that.isInstanceOf[TypeBoundMap[TypeBound, Key, Val]] &&
    that.asInstanceOf[TypeBoundMap[TypeBound, Key, Val]].underlying == underlying

}
