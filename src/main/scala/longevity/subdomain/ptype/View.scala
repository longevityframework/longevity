package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.ViewItem

/** a type class for views */
abstract class View[V <: ViewItem](
  implicit viewTypeKey: TypeKey[V],
  shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[V]()(viewTypeKey, shorthandPool)

