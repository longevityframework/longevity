import scala.language.implicitConversions
import scala.reflect.runtime.universe.TypeTag

/** a collection of utilities for reflecting on types */
package object emblem {

  /** returns a [[TypeKey]] for the specified type `A`. this method will only work where a `TypeTag` is
   * implicitly available. */
  def typeKey[A : TypeKey]: TypeKey[A] = implicitly[TypeKey[A]]

  /** an implicit method for producing a [[TypeKey]]. this method allows type keys to be available implicitly
   * anywhere that the corresponding `TypeTag` is implicitly available. */
  implicit def typeKeyFromTag[A : TypeTag]: TypeKey[A] = TypeKey(implicitly[TypeTag[A]])

  /** creates and returns an [[Emblem]] for the specified type `A`. `A` must be a case class with a single
   * parameter list. */
  @throws[EmblemGenerator.EmblemGeneratorException]
  def emblemFor[A <: HasEmblem : TypeKey]: Emblem[A] = new EmblemGenerator[A].generate

}
