package longevity.model

import emblem.TypeKey
import emblem.typeKey
import scala.reflect.runtime.universe.TypeTag

/** a type class for a persistent component
 *
 * @tparam M the domain model
 * @tparam C the persistent class
 */
abstract class CType[M : ModelEv, C : TypeTag] {

  private[longevity] val cTypeKey: TypeKey[C] = typeKey[C]

  override def toString = s"CType[${cTypeKey.name}]"

}

/** contains a factory method for creating `CTypes` */
object CType {

  /** create and return a `CType` for type `C`
   *
   * @tparam M the domain model
   * @tparam C the component class
   */
  def apply[M : ModelEv, C : TypeTag] = new CType[M, C] {}

}
