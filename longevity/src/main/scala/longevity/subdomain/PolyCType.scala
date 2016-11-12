package longevity.subdomain

import emblem.TypeKey

/** the base type for a family of component types. use as your [[CType]] when
 * it represents an abstract component type with concrete subtypes.
 */
trait PolyCType[Poly] extends CType[Poly]

/** contains a factory method for creating `PolyCTypes` */
object PolyCType {

  /** create and return an `PolyCType` for type `Poly` */
  def apply[Poly : TypeKey] = new PolyCType[Poly] {}

}
