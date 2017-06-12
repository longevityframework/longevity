package longevity.emblem.exceptions

import typekey.TypeKey

/** an exception thrown when a [[emblem.emblematic.traversors.sync.Visitor
 * Visitor]] cannot visit requested data due to encountering an unsupported type
 */
private[longevity] class CouldNotVisitException(val typeKey: TypeKey[_], cause: CouldNotTraverseException)
extends TraversorException(s"don't know how to visit type ${typeKey.tpe}", cause)
