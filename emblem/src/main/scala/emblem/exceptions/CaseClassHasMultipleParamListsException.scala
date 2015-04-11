package emblem.exceptions

import emblem.TypeKey

/** this exception is thrown when a user tries to generate an [[emblem.Emblem Emblem]] or an [[emblem.Extractor
 * Extractor]] for a case class that has multiple parameter lists.
 *
 * `Emblem` and `Extractor` generation for non-case class types may be supported in the future. these types would
 * have to meet some other criteria, to assure that we can construct properties and builders in a well-behaved
 * manner.
 */
class CaseClassHasMultipleParamListsException(key: TypeKey[_])
extends GeneratorException(
  key,
  s"generation for case classes with extra param lists currently not supported: $key")
