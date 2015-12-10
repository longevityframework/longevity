package emblem.exceptions

import emblem._

/** an exception thrown when the user attempts to build an [[EmblemPropPath]] where one of the
 * steps in the specified path is a collection type
 */
class CollectionInPropPathException[A](
  val emblem: Emblem[_],
  val fullPath: String,
  val collectionPathSegment: String)(
  implicit val typeKey: TypeKey[A])
extends EmblemPropPathException(
  s"property path $fullPath for emblem $emblem specifies collection $collectionPathSegment. " +
  s"collections are not currently supported in emblem prop paths.")
