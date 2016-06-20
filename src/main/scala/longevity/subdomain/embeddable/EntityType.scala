package longevity.subdomain.embeddable

import emblem.TypeKey

/** a type class for a [[Entity domain entity]] */
abstract class EntityType[E <: Entity : TypeKey] extends EType[E]
