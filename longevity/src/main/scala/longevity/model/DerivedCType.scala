package longevity.model

import typekey.TypeKey
import typekey.typeKey
import scala.reflect.runtime.universe.TypeTag

/** one of the derived types in a family of component types. use this as your
 * [[CType]] when it represents a concrete subtype of a [[PolyCType]].
 * 
 * @tparam M the domain model
 * @tparam C the component class
 * @tparam Poly the parent component class
 */
abstract class DerivedCType[M : ModelEv, C : TypeTag, Poly >: C : TypeTag] extends CType[M, C] {

  private[longevity] val polyTypeKey: TypeKey[Poly] = typeKey[Poly]

  override def toString = s"DerivedCType[${cTypeKey.name}, ${polyTypeKey.name}]"

}

/** contains a factory method for creating `DerivedCTypes` */
object DerivedCType {

  /** create and return a `DerivedCType` for types `C` and `Poly`
   * 
   * @tparam M the domain model
   * @tparam C the component class
   * @tparam Poly the parent component class
   */
  def apply[M : ModelEv, C : TypeTag, Poly >: C : TypeTag] = new DerivedCType[M, C, Poly] {
  }

}
