package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.PType

/** a type class for views */
abstract class View[V](implicit viewTypeKey: TypeKey[V]) extends PType[V]()(viewTypeKey)

