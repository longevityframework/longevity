import emblem.factories.EmblemFactory
import emblem.factories.ExtractorFactory
import scala.reflect.runtime.universe.TypeTag

/** a collection of utilities for reflecting on types */
package object emblem {

  /** returns a [[TypeKey]] for the specified type `A`. this method will only work where a `TypeTag` is
   * implicitly available. */
  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  /** an implicit method for producing a [[TypeKey]]. this method allows type keys to be available implicitly
   * anywhere that the corresponding `TypeTag` is implicitly available. */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

  /** creates and returns an [[Emblem]] for the specified type `A`. `A` must be a stable case class with a single
   * parameter list.
   * @throws emblem.exceptions.GeneratorException when `A` is not a stable case class with a single
   * parameter list.
   */
  def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = new EmblemFactory[A].generate

  /** A [[TypeKeyMap]] of [[HasEmblem]] to [[Emblem]] */
  type EmblemPool = TypeKeyMap[HasEmblem, Emblem]  

  // TODO cleanup comment add tparams
  /** creates and returns an [[Extractor]] for the specified types `Range` and `Domain`. `A` must be a
   * stable case class with single a parameter list.
   * @throws emblem.exceptions.GeneratorException when `A` is not a stable case class with a single
   * parameter list
   */
  def extractorFor[Domain : TypeKey, Range : TypeKey]: Extractor[Domain, Range] =
    new ExtractorFactory[Domain, Range].generate

  /** An extractor with the domain type unspecified. this type is equivalent to Extractor[Range, _],
   * except with a single type parameter Range. this allows it to be used as a key in a TypeBoundMap */
  type ExtractorFor[Range] = Extractor[_, Range]

  /** A [[TypeKeyMap]] of `Range` to [[Extractor]] */
  type ExtractorPool = TypeKeyMap[Any, ExtractorFor]

  /** a no-arg function with return type A */
  type Function0[A] = () => A



  // TODO pt 86950564: an extension class for Map with a toTypeKeyMap[B,V] method
  // TODO pt 86950588: an extension class for Map with a toTypeBoundMap[B,K,V] method

}
