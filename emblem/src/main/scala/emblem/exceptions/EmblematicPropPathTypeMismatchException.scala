package emblem.exceptions

import emblem.TypeKey
import emblem.emblematic.Emblematic

/** an exception thrown when the user attempts to build an
 * [[emblem.emblematic.EmblematicPropPath EmblematicPropPath]] with a specified
 * type, but the actual type of the path is something else
 */
class EmblematicPropPathTypeMismatchException(
  val emblematic: Emblematic,
  val fullPath: String,
  val rootTypeKey: TypeKey[_],
  val requestedTypeKey: TypeKey[_],
  val actualTypeKey: TypeKey[_])
extends EmblematicPropPathException(
  s"property path $fullPath for type ${rootTypeKey.name} has type ${actualTypeKey.name}, " +
  s"but type ${requestedTypeKey.name} was requested")
