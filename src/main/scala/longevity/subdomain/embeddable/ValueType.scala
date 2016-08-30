package longevity.subdomain.embeddable

import emblem.TypeKey

/** a value type. functionally equivalent to an [[EntityType]] */
abstract class ValueType[A <: ValueObject : TypeKey] extends EType[A]

/** contains a factory method for creating `ValueTypes` */
object ValueType {

  /** create and return an `ValueType` for type `A` */
  def apply[A <: ValueObject : TypeKey] = new ValueType[A] {
  }

}
