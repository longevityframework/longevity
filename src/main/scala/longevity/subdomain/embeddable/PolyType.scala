package longevity.subdomain.embeddable

import emblem.TypeKey

/** the base type for a family of domain entity types. mix this in to your
 * [[EType]] when it represents an abstract embeddable type with concrete
 * subtypes.
 */
trait PolyType[Poly <: Embeddable] extends EType[Poly]

/** contains a factory method for creating `PolyTypes` */
object PolyType {

  /** create and return an `PolyType` for type `Poly` */
  def apply[Poly <: Embeddable : TypeKey] = new PolyType[Poly] {
  }

}
