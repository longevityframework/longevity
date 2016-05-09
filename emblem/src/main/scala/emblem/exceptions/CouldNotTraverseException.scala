package emblem.exceptions

import emblem.TypeKey

/** an exception thrown when an [[emblem.emblematic.traversors.async.Traversor
 * asynchronous Traversor]] or a [[emblem.emblematic.traversors.sync.Traversor
 * synchronous Traversor]] cannot visit requested data due to encountering an
 * unsupported type
 */
class CouldNotTraverseException(val typeKey: TypeKey[_])
extends TraversorException(s"don't know how to traverse type ${typeKey.tpe}")
