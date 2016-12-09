package longevity.exceptions.model.ptype

import longevity.exceptions.model.SubdomainException

/** an exception involving [[longevity.model.PType persistent type]]
 * creation or use
 */
class PTypeException(message: String, cause: Exception) extends SubdomainException(message, cause) {
  def this(message: String) { this(message, null) }
}
