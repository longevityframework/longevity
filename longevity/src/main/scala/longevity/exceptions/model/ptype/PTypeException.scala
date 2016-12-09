package longevity.exceptions.model.ptype

import longevity.exceptions.model.DomainModelException

/** an exception involving [[longevity.model.PType persistent type]]
 * creation or use
 */
class PTypeException(message: String, cause: Exception) extends DomainModelException(message, cause) {
  def this(message: String) { this(message, null) }
}
