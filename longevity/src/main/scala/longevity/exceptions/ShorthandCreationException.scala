package longevity.exceptions

import emblem.imports._
import emblem.exceptions.GeneratorException

class ShorthandCreationException private(
  message: String,
  cause: GeneratorException,
  val actualTypeKey: TypeKey[_],
  val abbreviatedTypeKey: TypeKey[_])
extends ShorthandException(message, cause) {

  def this(
    cause: GeneratorException,
    actualTypeKey: TypeKey[_],
    abbreviatedTypeKey: TypeKey[_]) {
    this(
      s"could not generate a shorthand for types $actualTypeKey and $abbreviatedTypeKey",
      cause,
      actualTypeKey,
      abbreviatedTypeKey)
  }

  def this(
    message: String,
    actualTypeKey: TypeKey[_],
    abbreviatedTypeKey: TypeKey[_]) {
    this(message, null, actualTypeKey, abbreviatedTypeKey)
  }

}
