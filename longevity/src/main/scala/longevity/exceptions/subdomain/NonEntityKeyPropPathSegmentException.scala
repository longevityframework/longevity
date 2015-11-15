package longevity.exceptions.subdomain

import emblem.TypeKey
import emblem.exceptions.NonEmblemInPropPathException

class NonEntityKeyPropPathSegmentException(
  val pathSegment: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  e: NonEmblemInPropPathException)
extends InvalidKeyPropPathException(
  s"non-leaf path segment $pathSegment is not an entity in path $path for root ${rootTypeKey.name}",
  e) {

  def this(pathSegment: String, path: String, rootTypeKey: TypeKey[_]) = this(pathSegment, path, rootTypeKey, null)

}

