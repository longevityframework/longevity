package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.TypeTag

/** an emblem property. the property belongs to an [[Emblem]], has a name, and a getter and a setter.
 * because the emblem is treated as an immutable object, the setter returns a new instance.
 *
 * @tparam A the type that the containing emblem reflects upon
 * @tparam B the property value type
 * @param name the property name
 * @param get a function that retrieves the property value from an instance
 * @param set a function that updates the property value to produce a new instance
 */
case class EmblemProp[A : TypeKey, B : TypeKey] private[emblem] (
  val name: String,
  val get: (A) => B,
  val set: (A, B) => A)
extends EmblemPropPath[A, B] {

  /** a [[TypeKey type key]] for the property value type */
  lazy val typeKey: TypeKey[B] = implicitly[TypeKey[B]]

  val props = this :: Nil

  override def toString: String = s"$name: ${typeKey.tpe}"

}
