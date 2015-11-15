package longevity.exceptions.persistence

import longevity.exceptions.LongevityException

/** an exception involving persistence */
class PersistenceException(message: String, cause: Exception) extends LongevityException(message, cause) {

  def this(message: String) { this(message, null) }

}
