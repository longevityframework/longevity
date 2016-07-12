package longevity.exceptions.subdomain.ptype

import emblem.TypeKey

class NoSuchPropPathException(
  val path: String,
  val pTypeKey: TypeKey[_])
extends PropException(
  s"no such property path '$path` in persistent ${pTypeKey.name}")

