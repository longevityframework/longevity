package longevity.exceptions.persistence

import emblem.TypeKey
import emblem.exceptions.ExtractorInverseException

/** an exception thrown when a [[longevity.persistence.Repo repository]] encounters an error while
 * unabbreviating a shorthand when deserializing an aggregate.
 */
class ShorthandUnabbreviationException(
  abbreviated: Any,
  actualTypeKey: TypeKey[_],
  cause: ExtractorInverseException)
extends TranslationException(
  s"could not unabbreviate $abbreviated into a $actualTypeKey",
  cause)
