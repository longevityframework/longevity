package longevity.emblem.exceptions

import typekey.TypeKey

/** an exception thrown when
 * [[emblem.emblematic.traversors.sync.TestDataGenerator TestDataGenerator]]
 * cannot generate requested data due to encountering an unsupported type
 */
private[longevity] class CouldNotGenerateException(val typeKey: TypeKey[_], cause: CouldNotTraverseException)
extends TraversorException(s"don't know how to generate test data for type ${typeKey.tpe}", cause)
