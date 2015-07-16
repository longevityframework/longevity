package emblem.exceptions

import emblem.TypeKey

/** an exception thrown by a [[emblem.traversors.async.Traversor Traversor]], or one of its cousins in the
 * [[emblem.traversors emblem.traversors package]], when invoking [[emblem.Extractor.inverse]] throws
 * exception
 */
class ExtractorInverseException(input: Any, domainTypeKey: TypeKey[_], cause: Exception)
extends TraversorException(
  s"Extractor.inverse threw exception on input $input when attempting to extract a ${domainTypeKey.tpe}", cause)
