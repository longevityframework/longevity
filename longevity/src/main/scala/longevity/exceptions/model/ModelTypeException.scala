package longevity.exceptions.model

import longevity.exceptions.UnrecoverableLongevityException
import longevity.exceptions.LongevityException

/** an exception involving [[longevity.model.ModelType model type]] creation or use */
class ModelTypeException(message: String, cause: Exception)
extends LongevityException(message, cause)
with UnrecoverableLongevityException {

  def this(message: String) { this(message, null) }

}
