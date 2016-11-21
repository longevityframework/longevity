package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** a type class for a persistent component */
abstract class CType[C : TypeKey] {

  /** a `TypeKey` for the component
   * @see `emblem.TypeKey`
   */
  val cTypeKey: TypeKey[C] = typeKey[C]

}

/** contains a factory method for creating `CTypes` */
object CType {

  /** create and return an `CType` for type `C` */
  def apply[C : TypeKey] = new CType[C] {}

}
