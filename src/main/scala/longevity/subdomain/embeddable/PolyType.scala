package longevity.subdomain.embeddable

/** the base type for a family of domain entity types. mix this in to your
 * [[EType]] when it represents an abstract embeddable type with concrete
 * subtypes.
 */
trait PolyType[Poly <: Embeddable] extends EType[Poly]
