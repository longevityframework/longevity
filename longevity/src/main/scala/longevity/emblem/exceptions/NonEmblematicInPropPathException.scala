package longevity.emblem.exceptions

import longevity.emblem.emblematic.Emblematic
import typekey.TypeKey

/** an exception thrown when the user attempts to build an
 * [[emblem.emblematic.EmblematicPropPath]] where one of the intermediate steps in the
 * specified path is something that is not covered by the [[emblem.emblematic.Emblematic]]
 */
private[longevity] class NonEmblematicInPropPathException[A](
  val emblematic: Emblematic,
  val fullPath: String,
  val nonEmblematicPathSegment: String)(
  implicit val typeKey: TypeKey[A])
extends EmblematicPropPathException(
  s"property path '$fullPath' for type ${typeKey.name} specifies a non-emblematic in the path")
