package emblem

import emblem.factories.EmblemFactory
import scala.reflect.runtime.universe.TypeTag

/** a reflective signature for a type. provides name information, [[EmblemProp
 * properties]], and a tool used to build new instances. the underlying type is
 * treated as immutable, so each property provides a setter that returns a new
 * instance. new instances can be built using a [[InstanceBuilder]]
 * returned by method [[builder]].
 *
 * @tparam A the type that this emblem reflects upon
 * @param typeKey a [[TypeKey type key]] for the type that this emblem reflects upon
 * @param props the [[EmblemProp emblem properties]]
 * @param creator a function used by the builder to instantiate the new object
 */
case class Emblem[A] private[emblem] (
  typeKey: TypeKey[A],
  props: Seq[EmblemProp[A, _]],
  creator: Map[String, Any] => A)
extends Reflective[A, EmblemProp] {

  /** a builder of instances of the type represented by this emblem */
  class InstanceBuilder private[Emblem] () {

    private var map = Map[String, Any]()

    /** specifies the value to use for the given property */
    def setProp[B](prop: EmblemProp[A, B], value: B): Unit = map += (prop.name -> value)

    /** builds and returns the instance */
    def build(): A = creator(map)
  }

  /** creates and returns a new builder for constructing new instances */
  def builder(): InstanceBuilder = new InstanceBuilder()

}

object Emblem {

  /** creates and returns an [[Emblem]] for the specified type `A`. `A` must be
   * a stable case class with a single parameter list.
   * 
   * @tparam A the type to create an emblem for
   * @throws emblem.exceptions.GeneratorException when `A` is not a stable case
   * class with a single parameter list
   */
  def apply[A : TypeKey]: Emblem[A] = new EmblemFactory[A].generate

}
