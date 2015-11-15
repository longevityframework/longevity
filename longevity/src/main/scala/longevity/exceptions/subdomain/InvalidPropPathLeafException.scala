package longevity.exceptions.subdomain

import emblem.TypeKey

class InvalidPropPathLeafException(
  val path: String,
  val rootTypeKey: TypeKey[_])
extends InvalidPropPathException(
  s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

