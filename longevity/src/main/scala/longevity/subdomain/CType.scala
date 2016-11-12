package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** a type class for a persistent component */
abstract class CType[E : TypeKey] {

  /** a `TypeKey` for the component
   * @see `emblem.TypeKey`
   */
  val eTypeKey: TypeKey[E] = typeKey[E]

}

/** contains a factory method for creating `CTypes` */
object CType {

  /** create and return an `CType` for type `E` */
  def apply[E : TypeKey] = new CType[E] {}

}
