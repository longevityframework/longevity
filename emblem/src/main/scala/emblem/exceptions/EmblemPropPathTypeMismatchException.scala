package emblem.exceptions

import emblem.TypeKey
import emblem.Emblem

/** an exception thrown when the user attempts to build an [[EmblemPropPath]] with a specified type,
 * but the actual type of the path is something else
 */
class EmblemPropPathTypeMismatchException(
  val emblem: Emblem[_],
  val fullPath: String,
  val requestedType: TypeKey[_],
  val actualType: TypeKey[_])
extends EmblemPropPathException(
  s"property path $fullPath for emblem $emblem has type ${actualType.name}, " +
  s"but type ${requestedType.name} was requested")
