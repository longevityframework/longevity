package longevity.subdomain

import emblem.TypeKey

/** one of the derived types in a family of component types. use this as your
 * [[CType]] when it represents a concrete subtype of a [[PolyCType]].
 */
abstract class DerivedCType[E : TypeKey, Poly >: E : TypeKey] extends CType[E] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

}

/** contains a factory method for creating `DerivedCTypes` */
object DerivedCType {

  /** create and return an `DerivedCType` for types `E` and `Poly` */
  def apply[E : TypeKey, Poly >: E : TypeKey] = new DerivedCType[E, Poly] {
  }

}
