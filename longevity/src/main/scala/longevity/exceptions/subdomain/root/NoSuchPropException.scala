package longevity.exceptions.subdomain.root

import emblem.TypeKey

class NoSuchPropException(
  val path: String,
  val rootTypeKey: TypeKey[_])
extends PropException(
  s"no such property '$path` root ${rootTypeKey.name}")

