package longevity.exceptions

/** an exception involving subdomain creation */
class SubdomainException(message: String, cause: Exception) extends LongevityException(message, cause) {

  def this(message: String) { this(message, null) }

}
