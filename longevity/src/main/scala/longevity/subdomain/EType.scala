package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** a type class for an [[Embeddable]] */
abstract class EType[E <: Embeddable : TypeKey] {

  /** a `TypeKey` for the domain entity
   * @see `emblem.TypeKey`
   */
  val eTypeKey: TypeKey[E] = typeKey[E]

}

/** contains a factory method for creating `ETypes` */
object EType {

  /** create and return an `EType` for type `E` */
  def apply[E <: Embeddable : TypeKey] = new EType[E] {
  }

}
