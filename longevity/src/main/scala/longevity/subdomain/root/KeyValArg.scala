package longevity.subdomain.root

import emblem.imports._

// TODO scaladocs
case class KeyValArg[A](value: A, typeKey: TypeKey[A])

object KeyValArg {

  implicit def keyValArg[A : TypeKey](value: A) = KeyValArg(value, typeKey[A])

}
