package longevity.exceptions.subdomain.root

import emblem.TypeKey

class InvalidPropPathLeafException(
  val path: String,
  val rootTypeKey: TypeKey[_])
extends PropException(
  s"key prop path $path for root ${rootTypeKey.name} is not a basic type, shorthand, or an assoc")

