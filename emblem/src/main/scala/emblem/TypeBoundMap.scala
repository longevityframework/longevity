package emblem

import scala.language.higherKinds

/** a map where the types for keys and values share a type parameter with the same bounds. the key and value
 * of each key/value pair are constrained to match on that type parameter. for example, we might have some pet
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
 * we can use a `TypeBoundMap` to store a list of pets of the appropriate type for every pet store:
 *
 * {{{
 * var inventories = TypeBoundMap[Pet, PetStore, List]
 * inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
 * inventories += (catStore2 -> List(Cat("cat21")))
 * inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))
 * }}}
 *
 * now we can look up pet lists by pet store, with everything coming back as the expected type:
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
 * note that the API does not provide `++` or similar methods to add multiple key/value pairs at a time, as
 * each pair needs to be type-checked separately.
 *
 * (the code presented here is in TypeBoundMapSpec.scala, up at the top)
 * 
 * @tparam TypeBound the upper bound on the type parameters passed to the Key and Val types
 * @tparam Key the parameterized type of the keys in the map
 * @tparam Val the parameterized type of the values in the map
 * 
 * @see TypeBoundMapSpec.scala and BaseTypeBoundMapSpec.scala for many more examples
 */
class TypeBoundMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] private (underlying: Map[Any, Any])
extends BaseTypeBoundMap[TypeBound, Key, Val](underlying) {

  /** retrieves the value which is associated with the given key, both bound by the same type param.
   * 
   * throws java.util.NoSuchElementException when no value is mapped to the supplied key
   * @tparam TypeParam the type param binding both the key and the value
   */
  def apply[TypeParam <: TypeBound](key: Key[TypeParam]): Val[TypeParam] = get(key).get

  /** optionally returns the value associated with the given key
   * @tparam TypeParam the type param bounding both the key and the value
   * @return an option value containing the value associated with type key in this map, or `None` if none
   * exists.
   */
  def get[TypeParam <: TypeBound](key: Key[TypeParam]): Option[Val[TypeParam]] =
    underlying.get(key).asInstanceOf[Option[Val[TypeParam]]]

  /** returns the value associated with a key, or a default value if the key is not contained in the map.
   *
   * @param default a computation that yields a default value in case no binding for the key is found in
   * the map
   * @tparam TypeParam the type param bounding both the key and the value
   * @return the value associated with key if it exists, otherwise the result of the `default` computation.
   */
  def getOrElse[TypeParam <: TypeBound](
    key: Key[TypeParam], default: => Val[TypeParam]): Val[TypeParam] =
    underlying.getOrElse(key, default).asInstanceOf[Val[TypeParam]]

  /** adds a key/value pair to this map, returning a new map. both the key and the value are bound by the same
   * type param.
   * 
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

  /** takes the union of two type bound maps with the same type params
   * @param that the type bound map to union with this type bound map
   * @return a new type bound map with the bindings of this map and that map
   */
  def ++(that: TypeBoundMap[TypeBound, Key, Val]): TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](underlying ++ that.underlying)

  /** selects all elements of this TypeBoundMap which satisfy a predicate
   *
   * @param p the predicate used to test elements
   * @return a new TypeBoundMap consisting of all elements of this TypeBoundMap
   * that satisfy the given predicate `p`. the order of the elements is
   * preserved.
   */
  def filter(p: TypeBoundPair[TypeBound, Key, Val, _ <: TypeBound] => Boolean)
  : TypeBoundMap[TypeBound, Key, Val] = {
    val underlyingP: ((Any, Any)) => Boolean = { pair =>
      type TypeParam = TP forSome { type TP <: TypeBound }
      val tbp = TypeBoundPair[TypeBound, Key, Val, TypeParam](
        pair._1.asInstanceOf[Key[TypeParam]],
        pair._2.asInstanceOf[Val[TypeParam]])
      p(tbp)
    }
    new TypeBoundMap[TypeBound, Key, Val](underlying.filter(underlyingP))
  }

  /** filters this map by retaining only keys satisfying a predicate
   *
   * @param p the predicate used to test keys
   * @return an immutable map consisting only of those key value pairs of this
   * map where the key satisfies the predicate `p`
   */
  def filterKeys(p: (Key[_ <: TypeBound]) => Boolean): TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](
      underlying.filterKeys { any => p(any.asInstanceOf[Key[_ <: TypeBound]]) })

  /** selects all elements of this TypeBoundMap which do not satisfy a predicate
   *
   * @param p the predicate used to test elements
   * @return a new TypeBoundMap consisting of all elements of this TypeBoundMap
   * that do not satisfy the given predicate `p`. the order of the elements is
   * preserved.
   */
  def filterNot(p: TypeBoundPair[TypeBound, Key, Val, _ <: TypeBound] => Boolean)
  : TypeBoundMap[TypeBound, Key, Val] =
    filter((pair) => !p(pair))

  /** filters this map by retaining only values satisfying a predicate
   * 
   * @param p the predicate used to test values
   * @return an immutable map consisting only of those key value pairs of this
   * map where the value satisfies the predicate `p`
   */
  def filterValues(p: (Val[_ <: TypeBound]) => Boolean): TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](
      underlying.filter { case (k, v) => p(v.asInstanceOf[Val[_ <: TypeBound]]) })

  override def hashCode = underlying.hashCode

  /** transforms this map by applying a function to every retrieved value.
   * @param f the function used to transform values of this map.
   * @return a map which maps every key of this map to `f(this(key))`.
   */
  def mapValues[
    Val2[_ <: TypeBound]](
    f: TypeBoundFunction[TypeBound, Val, Val2])
  : TypeBoundMap[TypeBound, Key, Val2] =
    new TypeBoundMap[TypeBound, Key, Val2](
      mapValuesUnderlying[TypeBound, Val2](f))

  /** a string representation of a TypeBoundMap */
  override def toString = s"TypeBound${underlying}"

  /** compares two maps structurally; i.e., checks if all mappings contained in this map are also contained in
   * the other map, and vice versa.
   * @param that the other type key map
   * @return true if both maps contain exactly the same mappings, false otherwise.
   */
  override def equals(that: Any) =
    that.isInstanceOf[TypeBoundMap[TypeBound, Key, Val]] &&
    that.asInstanceOf[TypeBoundMap[TypeBound, Key, Val]].underlying == underlying

}

object TypeBoundMap {

  /** creates and returns an empty [[TypeBoundMap]] for the supplied types.
   * @tparam TypeBound the upper bound on the type parameters passed into the Key and Value types
   * @tparam Key the parameterized type of the keys in the map
   * @tparam Val the parameterized type of the values in the map
   */
  def apply[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]](): TypeBoundMap[TypeBound, Key, Val] =
    new TypeBoundMap[TypeBound, Key, Val](Map.empty)

}
