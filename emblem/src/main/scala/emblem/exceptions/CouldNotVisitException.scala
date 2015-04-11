package emblem.exceptions

import emblem.TypeKey

/** an exception thrown when a [[emblem.traversors.Visitor Visitor]] cannot visit requested data due to
 * encountering an unsupported type.
 */
class CouldNotVisitException(val typeKey: TypeKey[_], cause: CouldNotTraverseException)
extends TraversorException(s"don't know how to visit type ${typeKey.tpe}", cause)
