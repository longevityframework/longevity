package emblem.exceptions

import emblem.TypeKey

/** an exception indicating you broke the contract of one of the factory methods [[emblem.Emblem]] and
 * [[emblem.Extractor]]
 */
abstract class GeneratorException(val key: TypeKey[_], message: String) extends EmblemException(message)
