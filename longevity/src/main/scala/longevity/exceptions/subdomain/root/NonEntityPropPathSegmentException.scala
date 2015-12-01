package longevity.exceptions.subdomain

import emblem.TypeKey
import emblem.exceptions.NonEmblemInPropPathException

class NonEntityPropPathSegmentException(
  val pathSegment: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  cause: NonEmblemInPropPathException)
extends PropException(
  s"non-leaf path segment $pathSegment is not an entity in path $path for root ${rootTypeKey.name}",
  cause) {

  def this(pathSegment: String, path: String, rootTypeKey: TypeKey[_]) = this(pathSegment, path, rootTypeKey, null)

}

