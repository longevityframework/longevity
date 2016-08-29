package longevity.subdomain.embeddable

import emblem.TypeKey

/** one of the derived types in a family of domain entity types. mix this in to
 * your [[EType]] when it represents a concrete subtype of a [[PolyType]].
 */
abstract class DerivedType[P <: Embeddable : TypeKey, Poly >: P <: Embeddable : TypeKey] extends EType[P] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

}
