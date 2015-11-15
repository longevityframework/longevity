package longevity.exceptions.subdomain

import emblem.TypeKey
import emblem.exceptions.NoSuchPropertyException

class NoSuchKeyPropPathSegmentException(
  val propName: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  e: NoSuchPropertyException)
extends InvalidKeyPropPathException(
  s"path segment $propName does not specify a property in path $path for root ${rootTypeKey.name}",
  e)

