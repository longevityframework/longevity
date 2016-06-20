package longevity.subdomain.embeddable

import emblem.TypeKey
import emblem.typeKey

/** a type class for an [[Embeddable]] */
abstract class EType[E <: Embeddable : TypeKey] {

  /** a `TypeKey` for the domain entity
   * @see `emblem.TypeKey`
   */
  val eTypeKey: TypeKey[E] = typeKey[E]

}
