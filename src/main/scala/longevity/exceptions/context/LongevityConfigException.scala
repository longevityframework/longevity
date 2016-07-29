package longevity.exceptions.context

import longevity.exceptions.LongevityException
import com.typesafe.config.ConfigException

/** an exception thrown when there was a problem constructing the
 * [[longevity.context.LongevityConfig LongevityConfig]]
 */
class LongevityConfigException(cause: ConfigException)
extends LongevityException(cause.getMessage, cause)
