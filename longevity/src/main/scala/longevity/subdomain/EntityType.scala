package longevity.subdomain

import emblem.imports._

/** a type class for a domain entity */
abstract class EntityType[E <: Entity : TypeKey] {

  // TODO pt-87441650 intra-entity contraints

  val entityTypeKey: TypeKey[E] = typeKey[E]

  val emblem: Emblem[E] = Emblem[E]

}
