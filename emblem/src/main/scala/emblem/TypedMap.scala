package emblem

import scala.language.higherKinds

object TypedMap {

  /** Creates and returns an empty [[TypedMap]] for the supplied types.
   * @tparam TypeBound the upper bound on the type parameters passed into the Key and Value types
   * @tparam Key the parameterized type of the keys in the map
   * @tparam Val the parameterized type of the values in the map
   */
  def apply[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]](): TypedMap[TypeBound, Key, Val] =
    new TypedMap[TypeBound, Key, Val](Map.empty)

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
 * We can use a `TypedMap` to store a list of pets of the appropriate type for every pet store:
 *
 * {{{
 * var inventories = TypedMap[Pet, PetStore, List]
 * inventories += (typeKey[Cat], catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
 * inventories += (typeKey[Cat], catStore2, Cat("cat21") :: Nil)
 * inventories += (typeKey[Dog], dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)
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
 * We need to explicitly specify the type parameter value when we add pairs to the map. Otherwise, the compiler
 * would infer a type parameter of `Pet` in the following example of a programmer's error we cannot allow to
 * compile:
 *
 * {{{
 * inventories += (dogStore1, Cat("cat11") :: Cat("cat12") :: Nil) // does not compile!
 * }}}
 *
 * If this were allowed to pass the compiler, we would get a runtime error (`ClassCastException`) when trying
 * to retrieve dogs out of the inventory for dog store 1.
 * 
 * Note that the API does not provide `++` or similar methods to add multiple key/value pairs at a time, as
 * each pair needs to be type-checked separately.
 *
 * (Code presented here is in TypedMapSpec.scala)
 * 
 * @tparam TypeBound the upper bound on the type parameters passed to the Key and Val types
 * @tparam Key the parameterized type of the keys in the map
 * @tparam Val the parameterized type of the values in the map
 * 
 * @see TypedMapSpec.scala and BaseTypedMapSpec.scala for many more examples
 */
class TypedMap[TypeBound, Key[_ <: TypeBound], Val[_ <: TypeBound]] private (map: Map[Any, Any])
extends BaseTypedMap[TypeBound, Key, Val](map) {

  // TODO scaladoc

  // note that this key cannot be implicit
  @throws[NoSuchElementException]
  def apply[TypeParam <: TypeBound : TypeKey](key: Key[TypeParam]): Val[TypeParam] = get(key).get

  def get[TypeParam <: TypeBound : TypeKey](key: Key[TypeParam]): Option[Val[TypeParam]] =
    map.get(key).asInstanceOf[Option[Val[TypeParam]]]

  def getOrElse[TypeParam <: TypeBound : TypeKey](
    key: Key[TypeParam], default: => Val[TypeParam]): Val[TypeParam] =
    map.getOrElse(key, default).asInstanceOf[Val[TypeParam]]

  // we need an explicit type key here, or the compiler will just infer TypeParam =:= TypeBound!
  def +[
    TypeParam <: TypeBound : TypeKey](
    keyedPair: ((TypeKey[TypeParam], Key[TypeParam]), Val[TypeParam]))
  : TypedMap[TypeBound, Key, Val] =
    new TypedMap[TypeBound, Key, Val](map + (keyedPair._1._2 -> keyedPair._2))

  // we need an explicit type key here, or the compiler will just infer TypeParam =:= TypeBound!
  def +[
    TypeParam <: TypeBound : TypeKey](
    typeKey: TypeKey[TypeParam],
    key: Key[TypeParam],
    value: Val[TypeParam])
  : TypedMap[TypeBound, Key, Val] =
    new TypedMap[TypeBound, Key, Val](map + (key -> value))

  override def toString = s"Typed${map}"

}
