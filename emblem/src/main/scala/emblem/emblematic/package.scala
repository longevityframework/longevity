package emblem

/** a collection of tools for describing collections of related data types in a
 * generic and traversable way
 */
package object emblematic {

  /** a [[TypeKeyMap]] of [[Emblem emblems]] */
  type EmblemPool = TypeKeyMap[Any, Emblem]

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

}
