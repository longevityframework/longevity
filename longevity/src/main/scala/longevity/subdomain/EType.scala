package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** a type class for a persistent component */
abstract class EType[E : TypeKey] {

  /** a `TypeKey` for the component
   * @see `emblem.TypeKey`
   */
  val eTypeKey: TypeKey[E] = typeKey[E]

}

/** contains a factory method for creating `ETypes` */
object EType {

  /** create and return an `EType` for type `E` */
  def apply[E : TypeKey] = new EType[E] {}

}
