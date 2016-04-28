package emblem.exceptions

import emblem.Emblematic
import emblem.TypeKey

/** an exception thrown when the user attempts to build an
 * [[emblem.EmblematicPropPath]] where one of the intermediate steps in the
 * specified path is something that is not covered by the [[emblem.Emblematic]]
 */
class NonEmblematicInPropPathException[A](
  val emblematic: Emblematic,
  val fullPath: String,
  val nonEmblematicPathSegment: String)(
  implicit val typeKey: TypeKey[A])
extends EmblematicPropPathException(
  s"property path '$fullPath' for type ${typeKey.name} specifies a non-emblematic in the path")
