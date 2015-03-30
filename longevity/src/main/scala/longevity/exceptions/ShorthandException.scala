package longevity.exceptions

/** an exception involving shorthand creation or usage */
class ShorthandException(message: String, cause: Exception) extends LongevityException(message, cause) {

  def this(message: String) { this(message, null) }

}
