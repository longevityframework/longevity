import scala.language.implicitConversions
import scala.reflect.runtime.universe.TypeTag
import emblem.exceptions._

/** a collection of utilities for reflecting on types */
package object emblem {

  /** returns a [[TypeKey]] for the specified type `A`. this method will only work where a `TypeTag` is
   * implicitly available. */
  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  /** an implicit method for producing a [[TypeKey]]. this method allows type keys to be available implicitly
   * anywhere that the corresponding `TypeTag` is implicitly available. */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

  /** creates and returns an [[Emblem]] for the specified type `A`. `A` must be a stable case class with a single
   * parameter list. */
  @throws[GeneratorException]
  def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = new EmblemGenerator[A].generate

  /** A [[TypeKeyMap]] of [[HasEmblem]] to [[Emblem]] */
  type EmblemPool = TypeKeyMap[HasEmblem, Emblem]  

  /** creates and returns an [[Shorthand]] for the specified type `A`. `A` must be a stable case class with
   * single a parameter list. */
  @throws[GeneratorException]
  def shorthandFor[Long : TypeKey, Short : TypeKey]: Shorthand[Long, Short] =
    new ShorthandGenerator[Long, Short].generate

  /** A shorthand with the short type unspecified. this type is equivalent to Shorthand[Long, _], except with
   * a single type parameter Long. this allows it to be used as a key in a TypeBoundMap */
  type ShorthandFor[Long] = Shorthand[Long, _]

  /** A [[TypeKeyMap]] of `Long` to [[Shorthand]] */
  type ShorthandPool = TypeKeyMap[Any, ShorthandFor]

  /** a no-arg function with return type A */
  type Function0[A] = () => A

  // TODO: an extension class for Map with a toTypeKeyMap[B,V] method
  // TODO: an extension class for Map with a toTypeBoundMap[B,K,V] method

}
