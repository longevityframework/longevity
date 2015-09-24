package emblem.exceptions

import emblem.Emblem

/** an exception thrown when the user attempts to build an [[EmblemPropPath]] where one of the
 * intermediate steps in the specified path is not something that extends [[HasEmblem]]
 */
class NonEmblemInPropPathException(
  val emblem: Emblem[_],
  val fullPath: String,
  val nonEmblemPathSegment: String)
extends EmblemPropPathException(
  "property path $fullPath for emblem $emblem specifies a non-emblem in the middle of the path")
