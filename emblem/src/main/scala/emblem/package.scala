import scala.reflect.runtime.universe.TypeTag

/** a collection of utilities for reflecting on types */
package object emblem {

  /** returns a [[TypeKey]] for the specified type `A`. this method will only work where a `TypeTag` is
   * implicitly available.
   */
  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  /** an implicit method for producing a [[TypeKey]]. this method allows type keys to be available implicitly
   * anywhere that the corresponding `TypeTag` is implicitly available.
   */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

  /** a [[TypeKeyMap]] of [[HasEmblem]] to [[Emblem]] */
  type EmblemPool = TypeKeyMap[HasEmblem, Emblem]  

  /** an [[Extractor extractor]] with the domain type unspecified. this type is
   * equivalent to `Extractor[Domain, _]`, except with a single type parameter
   * `Domain`. this allows the extractor to be used as a key or value in a
   * `TypeBoundMap` or `TypeKeyMap`
   * 
   * @see ExtractorPool
   */
  type ExtractorFor[Domain] = Extractor[Domain, _]

  /** A [[TypeKeyMap]] of `Domain` to [[Extractor]] */
  type ExtractorPool = TypeKeyMap[Any, ExtractorFor]

  /** a no-arg function with return type `A` */
  type Function0[A] = () => A

  /** A [[TypeKeyMap]] of `Any` to [[Union]] */
  type UnionPool = TypeKeyMap[Any, Union]

  // TODO pt-86950564: an extension class for Map with a toTypeKeyMap[B,V] method
  // TODO pt-86950588: an extension class for Map with a toTypeBoundMap[B,K,V] method

}
