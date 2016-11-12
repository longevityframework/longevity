package longevity.subdomain

import emblem.TypeKey

/** the base type for a family of component types. use as your [[EType]] when
 * it represents an abstract component type with concrete subtypes.
 */
trait PolyEType[Poly] extends EType[Poly]

/** contains a factory method for creating `PolyETypes` */
object PolyEType {

  /** create and return an `PolyEType` for type `Poly` */
  def apply[Poly : TypeKey] = new PolyEType[Poly] {}

}
