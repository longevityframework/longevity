package longevity.exceptions.subdomain.root

import emblem.TypeKey
import emblem.exceptions.NoSuchPropertyException

// TODO NoSuchPathSegmentE

class NoSuchPropPathSegmentException(
  val propName: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  cause: NoSuchPropertyException)
extends PropException(
  s"path segment $propName does not specify a property in path $path for root ${rootTypeKey.name}",
  cause)

