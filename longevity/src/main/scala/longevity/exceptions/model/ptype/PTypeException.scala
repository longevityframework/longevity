package longevity.exceptions.model.ptype

import longevity.exceptions.model.ModelTypeException

/** an exception involving [[longevity.model.PType persistent type]]
 * creation or use
 */
class PTypeException(message: String, cause: Exception) extends ModelTypeException(message, cause) {
  def this(message: String) { this(message, null) }
}
