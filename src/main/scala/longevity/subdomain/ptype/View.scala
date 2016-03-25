package longevity.subdomain.ptype

import emblem.imports._
import longevity.subdomain.ShorthandPool
import longevity.subdomain.persistent.ViewItem

/** a type class for views */
abstract class View[
  V <: ViewItem](
  implicit private val pTypeKey: TypeKey[V],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[V]
