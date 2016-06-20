package longevity.subdomain.embeddable

/** the base type for a family of domain entity types. mix this in to your
 * [[EntityType]] when it represents an abstract entity type with concrete
 * subtypes.
 */
trait PolyType[Poly <: Entity] extends EntityType[Poly]
