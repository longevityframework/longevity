package longevity.subdomain

import emblem.TypeKey

/** one of the derived types in a family of component types. use this as your
 * [[CType]] when it represents a concrete subtype of a [[PolyCType]].
 */
abstract class DerivedCType[C : TypeKey, Poly >: C : TypeKey] extends CType[C] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

}

/** contains a factory method for creating `DerivedCTypes` */
object DerivedCType {

  /** create and return an `DerivedCType` for types `C` and `Poly` */
  def apply[C : TypeKey, Poly >: C : TypeKey] = new DerivedCType[C, Poly] {
  }

}
