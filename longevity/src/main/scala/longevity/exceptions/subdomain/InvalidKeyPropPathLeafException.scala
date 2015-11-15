package longevity.exceptions.subdomain

import emblem.TypeKey

class InvalidKeyPropPathLeafException(
  val path: String,
  val rootTypeKey: TypeKey[_])
extends InvalidKeyPropPathException(
  s"nat key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

