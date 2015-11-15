package longevity.exceptions.subdomain

import emblem.typeKey
import emblem.TypeKey
import emblem.exceptions.EmblemPropPathTypeMismatchException

class PropTypeMismatchException[A : TypeKey](
  val path: String,
  val rootTypeKey: TypeKey[_])
extends InvalidPropPathException(
  s"type for path $path did not match requested type ${typeKey[A].name} for root ${rootTypeKey.name}")
