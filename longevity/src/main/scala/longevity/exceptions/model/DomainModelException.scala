package longevity.exceptions.model

import longevity.exceptions.UnrecoverableLongevityException
import longevity.exceptions.LongevityException

/** an exception involving [[longevity.model.DomainModel domain model]] creation or use */
class DomainModelException(message: String, cause: Exception)
extends LongevityException(message, cause)
with UnrecoverableLongevityException {

  def this(message: String) { this(message, null) }

}
