package longevity.exceptions.persistence

import emblem.TypeKey

/** an exception thrown when a [[longevity.persistence.Repo repository]] encounters an error while
 * unabbreviating a shorthand when deserializing an aggregate.
 */
class ShorthandUnabbreviationException(
  abbreviated: Any,
  actualTypeKey: TypeKey[_],
  cause: Exception)
extends TranslationException(
  s"could not unabbreviate $abbreviated into a $actualTypeKey",
  cause)
