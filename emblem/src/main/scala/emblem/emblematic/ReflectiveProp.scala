package emblem.emblematic

import emblem.TypeKey

/** a property for an [[Emblem]] or a [[Union]]. the property has a name and a
 * getter. unlike [[EmblemProp]], there is no setter here, because there is no
 * generic way to produce a new instance of a `Union` type.
 *
 * @tparam A the type that has the property
 * @tparam B the type of the property value
 */
trait ReflectiveProp[A, B] {

  /** a [[TypeKey type key]] for the property value */
  val typeKey: TypeKey[B]

  /** the property name */
  val name: String

  /** a function that retrieves the property value from an instance */
  val get: (A) => B

  override def toString: String = s"$name: ${typeKey.tpe}"

}
