package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.ddd.subdomain.ViewItem

/** a type class for views */
abstract class View[V <: ViewItem](implicit viewTypeKey: TypeKey[V])
extends PType[V]()(viewTypeKey)

