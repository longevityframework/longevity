package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.Root

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootType[
  R <: Root](
  implicit private val rootTypeKey: TypeKey[R],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[R]
