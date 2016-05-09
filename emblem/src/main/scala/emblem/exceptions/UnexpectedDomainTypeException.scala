package emblem.exceptions

import emblem.TypeKey

/** an exception thrown when the `Domain` type supplied to
 * [[emblem.emblematic.Extractor Extractor]] does not match the type of the
 * single parameter of the `Range` case class
 */
class UnexpectedDomainTypeException(domainTypeKey: TypeKey[_], val rangeTypeKey: TypeKey[_])
extends GeneratorException(
  domainTypeKey,
  s"could not generate a extractor for case class $domainTypeKey with short type $rangeTypeKey")
