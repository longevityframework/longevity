package emblem.exceptions

import emblem._

/** an exception thrown when the user attempts to build an [[EmblemPropPath]] where one of the
 * intermediate steps in the specified path is not something that extends [[HasEmblem]]
 */
class NonEmblemInPropPathException[A](
  val emblem: Emblem[_],
  val fullPath: String,
  val nonEmblemPathSegment: String)(
  implicit val typeKey: TypeKey[A])
extends EmblemPropPathException(
  s"property path $fullPath for emblem $emblem specifies a non-emblem in the middle of the path")
