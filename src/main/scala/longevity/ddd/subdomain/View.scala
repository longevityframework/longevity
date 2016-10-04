package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.ptype.PType

/** a type class for views */
abstract class View[V <: ViewItem](implicit viewTypeKey: TypeKey[V])
extends PType[V]()(viewTypeKey)

