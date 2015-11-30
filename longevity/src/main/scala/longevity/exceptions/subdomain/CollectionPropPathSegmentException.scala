package longevity.exceptions.subdomain

import emblem.TypeKey
import emblem.exceptions.CollectionInPropPathException

class CollectionPropPathSegmentException(
  val pathSegment: String,
  val path: String,
  val rootTypeKey: TypeKey[_],
  cause: CollectionInPropPathException)
extends PropException(
  s"property path '$path' for root '${rootTypeKey.name}' specifies collection '$pathSegment'. " +
  s"collections are not currently supported in prop paths.",
  cause)
