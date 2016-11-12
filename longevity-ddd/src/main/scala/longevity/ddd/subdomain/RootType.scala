package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.PType

/** a type class for a domain entity that serves as an aggregate root */
abstract class RootType[R](implicit private val rootTypeKey: TypeKey[R]) extends PType[R]
