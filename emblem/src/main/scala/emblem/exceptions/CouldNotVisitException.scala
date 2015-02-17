package emblem.exceptions

import emblem.TypeKey

/** an exception thrown when [[emblem.traversors.Visitor]] cannot visit requested data due to
 * encountering an unsupported type. */
class CouldNotVisitException(val typeKey: TypeKey[_], cause: CouldNotTraverseException)
extends Exception(s"don't know how to visit type ${typeKey.tpe}", cause)
