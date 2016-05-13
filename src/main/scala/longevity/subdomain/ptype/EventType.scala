package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.persistent.Event

/** a type class for events */
abstract class EventType[E <: Event](
  implicit eventTypeKey: TypeKey[E])
extends PType[E]()(eventTypeKey)
