package emblem.exceptions

import emblem.TypeKey

/** an exception indicating you broke the contract of one of the
 * [[emblem.emblematic.Emblem]] factory methods
 */
abstract class GeneratorException(val key: TypeKey[_], message: String) extends EmblemException(message)
