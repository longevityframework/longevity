package longevity.subdomain.embeddable

/** one of the derived types in a family of domain entity types. mix this in to
 * your [[EType]] when it represents a concrete subtype of a [[PolyType]].
 */
trait DerivedType[P <: Embeddable, Poly >: P <: Embeddable] extends EType[P] {

  /** the poly type that this type is derived from */
  val polyType: PolyType[Poly]

}
