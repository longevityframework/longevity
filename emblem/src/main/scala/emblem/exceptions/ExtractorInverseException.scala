package emblem.exceptions

import emblem.TypeKey

/** an exception thrown by a [[emblem.emblematic.traversors.async.Traversor
 * Traversor]], or one of its cousins in the [[emblem.emblematic.traversors
 * emblem.emblematic.traversors package]], when invoking
 * [[emblem.emblematic.Extractor.inverse]] throws exception
 */
class ExtractorInverseException(input: Any, domainTypeKey: TypeKey[_], cause: Exception)
extends TraversorException(
  s"Extractor.inverse threw exception on input $input when attempting to extract a ${domainTypeKey.tpe}", cause)
