package longevity.subdomain.root

import emblem.imports._

/** an argument used to construct a [[KeyVal]]. a value is wrapped up with its type, so we can check that
 * the value matches the type of the corresponding [[Prop]] when building the `KeyVal`.
 */
case class KeyValArg[A](value: A, typeKey: TypeKey[A])

object KeyValArg {

  /** implicitly converts a raw value into a [[KeyValArg]] */
  implicit def keyValArg[A : TypeKey](value: A) = KeyValArg(value, typeKey[A])

}
