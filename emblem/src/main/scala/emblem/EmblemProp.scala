package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.TypeTag
import stringUtil._

/** An emblem property. The property belongs to an [[Emblem]], has a name, and a getter and a setter.
 * Because the emblem is treated as an immutable object, the setter returns a new instance.
 *
 * @tparam T the type that the containing emblem reflects upon
 * @tparam U the property value type
 * @param name the property name
 * @param get a function that retrieves the property value from an instance
 * @param set a function that updates the property value to produce a new instance
 */
case class EmblemProp[T <: HasEmblem : TypeKey, U : TypeKey] private[emblem] (
  val name: String,
  val get: (T) => U,
  val set: (T, U) => T
) {

  /** A [[TypeKey type key]] for the property value type */
  lazy val typeKey: TypeKey[U] = implicitly[TypeKey[U]]

  override def toString: String = s"$name: ${typeKey.tpe}"

}
