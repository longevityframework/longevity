package longevity.exceptions.subdomain.ptype

import longevity.exceptions.subdomain.SubdomainException

/** an exception involving [[PType persistent type]] creation or use */
class PTypeException(message: String, cause: Exception) extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
