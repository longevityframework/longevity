package longevity.subdomain

import emblem.imports._

/** a type class for views */
abstract class View[
  V <: ViewItem](
  implicit private val pTypeKey: TypeKey[V],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[V]
