package longevity.model

import emblem.TypeKey
import emblem.typeKey
import scala.reflect.runtime.universe.TypeTag

/** one of the derived types in a family of component types. use this as your
 * [[CType]] when it represents a concrete subtype of a [[PolyCType]].
 */
abstract class DerivedCType[C : TypeTag, Poly >: C : TypeTag] extends CType[C] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = typeKey[Poly]

  override def toString = s"DerivedCType[${cTypeKey.name}, ${polyTypeKey.name}]"

}

/** contains a factory method for creating `DerivedCTypes` */
object DerivedCType {

  /** create and return a `DerivedCType` for types `C` and `Poly` */
  def apply[C : TypeTag, Poly >: C : TypeTag] = new DerivedCType[C, Poly] {
  }

}
