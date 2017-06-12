package longevity.emblem.emblematic

import typekey.TypeKey

/** an union property. the property belongs to an [[Union]], has a name, and a
 * getter and a setter.
 */
private[longevity] case class UnionProp[A, B : TypeKey] private[emblem] (
  name: String,
  get: (A) => B,
  set: (A, B) => A)
extends ReflectiveProp[A, B] {

  lazy val typeKey: TypeKey[B] = implicitly[TypeKey[B]]

  val isOnlyChild: Boolean = false

}
