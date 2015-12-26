package longevity.exceptions.subdomain

import emblem.imports._
import emblem.exceptions.GeneratorException

/** thrown when the input types supplied when attempting to create a shorthand are not legal */
class ShorthandCreationException private(
  message: String,
  cause: Exception,
  val actualTypeKey: TypeKey[_],
  val abbreviatedTypeKey: TypeKey[_])
extends ShorthandException(message, cause) {

  def this(
    cause: Exception,
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
