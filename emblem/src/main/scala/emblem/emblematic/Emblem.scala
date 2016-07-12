package emblem.emblematic

import emblem.TypeKey
import emblem.emblematic.factories.EmblemFactory
import emblem.emblematic.basicTypes.isBasicType
import emblem.exceptions.EmblemNotComposedOfBasicsException

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
extends Reflective[A] {

  type PropType[B, C] = EmblemProp[B, C]

  private[emblematic] val propsMap: Map[String, EmblemProp[A, _]] =
    props.map(prop => prop.name -> prop).toMap

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

  /** returns a sequence of all the [[basicTypes basic]] [[EmblematicPropPath
   * emblematic prop paths]] that compose this emblem. these prop paths are
   * ordered by their (depth-first) appearance in the emblem.
   *
   * @param emblematic the emblematic to use in the recursive descent
   * 
   * @throws emblem.exceptions.EmblemNotComposedOfBasicsException if the emblem
   * recursively contains any unions or collections
   */
  def basicPropPaths(emblematic: Emblematic): Seq[EmblematicPropPath[A, _]] = {
    val pathStrings = basicPropPathStrings(emblematic.emblems)
    pathStrings.map(EmblematicPropPath.unbounded(emblematic, _)(typeKey))
  }

  private def basicPropPathStrings(emblems: EmblemPool): Seq[String] = {
    props.foldLeft(Seq[String]()) { (propPaths, prop) =>
      val key = prop.typeKey
      if (isBasicType(key)) {
        propPaths :+ prop.name
      } else if (emblems.contains(key)) {
        propPaths ++ emblems(key).basicPropPathStrings(emblems).map {
          pathSuffix => s"${prop.name}.$pathSuffix"
        }
      } else {
        throw new EmblemNotComposedOfBasicsException(this, prop)
      }
    }

  }

  override def toString = s"Emblem[${typeKey.name}]"

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
