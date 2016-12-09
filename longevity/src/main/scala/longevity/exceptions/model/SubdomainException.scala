package longevity.exceptions.model

import longevity.exceptions.UnrecoverableLongevityException
import longevity.exceptions.LongevityException

/** an exception involving subdomain creation or use */
class SubdomainException(message: String, cause: Exception)
extends LongevityException(message, cause)
with UnrecoverableLongevityException {

  def this(message: String) { this(message, null) }

}
