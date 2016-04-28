package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** a type class for a domain entity */
abstract class EntityType[E <: Entity : TypeKey] {

  /** a `TypeKey` for the domain entity
   * @see `emblem.TypeKey`
   */
  val entityTypeKey: TypeKey[E] = typeKey[E]

}
