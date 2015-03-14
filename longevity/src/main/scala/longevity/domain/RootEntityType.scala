package longevity.domain

import emblem._

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootEntityType[E <: RootEntity : TypeKey] extends EntityType[E] {

  // TODO pt-84760388 natural keys

}
