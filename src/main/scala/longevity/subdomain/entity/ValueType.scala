package longevity.subdomain.entity

/** a value type. functionally equivalent to an [[EntityType]] */
trait ValueType[A <: ValueObject] extends EntityType[A]
