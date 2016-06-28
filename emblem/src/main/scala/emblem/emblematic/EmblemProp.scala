package emblem.emblematic

import emblem.TypeKey

/** an emblem property. the property belongs to an [[Emblem]], has a name, and a
 * getter and a setter. because the emblem is treated as an immutable object,
 * the setter returns a new instance.
 */
case class EmblemProp[A, B : TypeKey] private[emblem] (
  name: String,
  get: (A) => B,
  set: (A, B) => A,
  isOnlyChild: Boolean)
extends ReflectiveProp[A, B] {

  lazy val typeKey: TypeKey[B] = implicitly[TypeKey[B]]

}
