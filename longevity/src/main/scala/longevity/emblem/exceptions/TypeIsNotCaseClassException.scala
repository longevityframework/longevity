package longevity.emblem.exceptions

import typekey.TypeKey

/** this exception is thrown when a user tries to generate an [[emblem.emblematic.Emblem
 * Emblem]] for a type that is not a case class.
 *
 * Emblem generation for non-case class types may be supported
 * in the future. these types would have to meet some other criteria, to assure
 * that we can construct properties and builders in a well-behaved manner.
 */
private[longevity] class TypeIsNotCaseClassException(key: TypeKey[_])
extends GeneratorException(key, s"generation for non-case classes is currently not supported: $key")
