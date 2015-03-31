package longevity.exceptions

import emblem.imports._
import emblem.exceptions.GeneratorException

class ShorthandCreationException(
  cause: GeneratorException,
  actualTypeKey: TypeKey[_],
  abbreviatedTypeKey: TypeKey[_])
extends ShorthandException(
  s"could not generate a shorthand for types $actualTypeKey and $abbreviatedTypeKey",
  cause)
