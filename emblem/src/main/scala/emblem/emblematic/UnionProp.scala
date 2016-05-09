package emblem.emblematic

import emblem.TypeKey

/** an union property. the property belongs to an [[Union]], has a name, and a
 * getter and a setter.
 *
 * @tparam A the type that the containing union reflects upon
 * @tparam B the property value type
 */
case class UnionProp[A, B : TypeKey] private[emblem] (
  name: String,
  get: (A) => B)
extends ReflectiveProp[A, B] {

  lazy val typeKey: TypeKey[B] = implicitly[TypeKey[B]]

}
