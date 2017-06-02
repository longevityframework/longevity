package longevity.model

import scala.reflect.runtime.universe.TypeTag

/** the base type for a family of component types. use as your [[CType]] when
 * it represents an abstract component type with concrete subtypes.
 * 
 * @tparam M the domain model
 * @tparam C the component class
 */
trait PolyCType[M, C] extends CType[M, C] {

  override def toString = s"PolyCType[${cTypeKey.name}]"

}

/** contains a factory method for creating `PolyCTypes` */
object PolyCType {

  /** create and return a `PolyCType` for type `Poly`
   * 
   * @tparam M the domain model
   * @tparam C the component class
   */
  def apply[M : ModelEv, C : TypeTag] = new PolyCType[M, C] {}

}
