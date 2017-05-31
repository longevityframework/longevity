package longevity.model

import scala.reflect.runtime.universe.TypeTag

/** the base type for a family of component types. use as your [[CType]] when
 * it represents an abstract component type with concrete subtypes.
 */
trait PolyCType[Poly] extends CType[Poly] {

  override def toString = s"PolyCType[${cTypeKey.name}]"

}

/** contains a factory method for creating `PolyCTypes` */
object PolyCType {

  /** create and return a `PolyCType` for type `Poly` */
  def apply[Poly : TypeTag] = new PolyCType[Poly] {}

}
