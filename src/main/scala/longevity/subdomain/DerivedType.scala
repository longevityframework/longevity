package longevity.subdomain

/** one of the derived types in a family of domain entity types. mix this in to
 * your [[EntityType]] when it represents a concrete subtype of a [[BaseType]].
 */
trait DerivedType[Base <: Entity, Derived <: Base] {
  self: EntityType[Derived] =>

  /** the base type that this type is derived from */
  val baseType: BaseType[Base]

}
