package longevity.subdomain

import emblem.TypeKey

/** one of the derived types in a family of domain entity types. mix this in to
 * your [[EType]] when it represents a concrete subtype of a [[PolyEType]].
 */
abstract class DerivedEType[E <: Embeddable : TypeKey, Poly >: E <: Embeddable : TypeKey] extends EType[E] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

}

/** contains a factory method for creating `DerivedETypes` */
object DerivedEType {

  /** create and return an `DerivedEType` for types `E` and `Poly` */
  def apply[E <: Embeddable : TypeKey, Poly >: E <: Embeddable : TypeKey] = new DerivedEType[E, Poly] {
  }

}
