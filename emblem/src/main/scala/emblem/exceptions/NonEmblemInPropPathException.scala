package emblem.exceptions

import emblem._

// TODO: this is going to have to morph into NonEmblematicInPropPathE

/** an exception thrown when the user attempts to build an [[EmblematicPropPath]]
 * where one of the intermediate steps in the specified path is something that
 * is not covered by the [[emblem.Emblematic]]
 */
class NonEmblemInPropPathException[A](
  val emblem: Emblem[_],
  val fullPath: String,
  val nonEmblemPathSegment: String)(
  implicit val typeKey: TypeKey[A])
extends EmblematicPropPathException(
  s"property path $fullPath for emblem $emblem specifies a non-emblem in the middle of the path")
