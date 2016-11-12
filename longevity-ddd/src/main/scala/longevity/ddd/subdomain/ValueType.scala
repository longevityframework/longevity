package longevity.ddd.subdomain

import emblem.TypeKey
import longevity.subdomain.CType

/** a value type. functionally equivalent to an [[EntityType]] */
abstract class ValueType[A : TypeKey] extends CType[A]

/** contains a factory method for creating `ValueTypes` */
object ValueType {

  /** create and return an `ValueType` for type `A` */
  def apply[A : TypeKey] = new ValueType[A] {}

}
