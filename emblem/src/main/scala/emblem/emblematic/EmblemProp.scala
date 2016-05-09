package emblem.emblematic

import emblem.TypeKey

/** an emblem property. the property belongs to an [[Emblem]], has a name, and a
 * getter and a setter. because the emblem is treated as an immutable object,
 * the setter returns a new instance.
 *
 * @tparam A the type that the containing emblem reflects upon
 * @tparam B the property value type
 * @param name the property name
 * @param get a function that retrieves the property value from an instance
 * @param set a function that updates the property value to produce a new instance
 */
case class EmblemProp[A, B : TypeKey] private[emblem] (
  name: String,
  get: (A) => B,
  set: (A, B) => A)
extends ReflectiveProp[A, B] {

  lazy val typeKey: TypeKey[B] = implicitly[TypeKey[B]]

}
