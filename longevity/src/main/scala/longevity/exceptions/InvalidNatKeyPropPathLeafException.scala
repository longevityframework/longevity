package longevity.exceptions

import emblem.TypeKey

class InvalidNatKeyPropPathLeafException(
  val path: String,
  val rootTypeKey: TypeKey[_])
extends InvalidNatKeyPropPathException(
  s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

