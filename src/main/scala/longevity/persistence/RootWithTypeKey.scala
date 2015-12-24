package longevity.persistence

import emblem.imports._
import longevity.subdomain.Root

// TODO where does this class belong?
// TODO scaladocs
case class RootWithTypeKey[R <: Root](
  root: R,
  typeKey: TypeKey[R])
