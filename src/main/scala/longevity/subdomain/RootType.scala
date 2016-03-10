package longevity.subdomain

import emblem.imports._

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootType[
  R <: Root](
  implicit private val rootTypeKey: TypeKey[R],
  implicit private val shorthandPool: ShorthandPool = ShorthandPool.empty)
extends PType[R]
