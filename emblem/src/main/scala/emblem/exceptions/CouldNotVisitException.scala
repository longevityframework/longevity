package emblem.exceptions

import emblem.TypeKey

/** an exception thrown when [[emblem.visitors.Visitor]] cannot visit requested data due to
 * encountering an unsupported type. */
class CouldNotVisitException(val typeKey: TypeKey[_])
extends Exception(s"don't know how to visit type ${typeKey.tpe}")
