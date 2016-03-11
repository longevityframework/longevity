package longevity.subdomain

import emblem.imports._

/** a type class for events */
abstract class EventType[
  E <: Event](
  implicit private val eventTypeKey: TypeKey[E],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[E]
