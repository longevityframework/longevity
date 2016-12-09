package longevity.exceptions.test

import emblem.TypeKey
import emblem.typeKey
import longevity.exceptions.UnrecoverableLongevityException
import longevity.exceptions.LongevityException

/** an exception that occurs when asking the
 * [[longevity.test.TestDataGenerator]] to generate a type that is not part of
 * the [[longevity.model.Subdomain]]
 */
class CouldNotGenerateException[A : TypeKey]
extends LongevityException(
  s"could not generate ${typeKey[A].name} because it is not in the domain model")
with UnrecoverableLongevityException
