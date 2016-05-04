package longevity.subdomain

/** one of the derived types in a family of domain entity types. mix this in to
 * your [[EntityType]] when it represents a concrete subtype of a [[PolyType]].
 */
trait DerivedType[P <: Entity, Poly >: P <: Entity] extends EntityType[P] {

  /** the poly type that this type is derived from */
  val polyType: PolyType[Poly]

}
