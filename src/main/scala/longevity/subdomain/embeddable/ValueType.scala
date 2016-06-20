package longevity.subdomain.embeddable

import emblem.TypeKey

/** a value type. functionally equivalent to an [[EntityType]] */
abstract class ValueType[A <: ValueObject : TypeKey] extends EType[A]
