package longevity.exceptions.subdomain.ptype

import emblem.TypeKey

class NoSuchPropException(
  val path: String,
  val pTypeKey: TypeKey[_])
extends PropException(
  s"no such property '$path` in persistent ${pTypeKey.name}")

