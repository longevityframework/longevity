package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.Event

/** a type class for events */
abstract class EventType[
  E <: Event](
  implicit private val eventTypeKey: TypeKey[E],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[E]
