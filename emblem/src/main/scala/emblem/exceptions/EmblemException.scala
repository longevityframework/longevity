package emblem.exceptions

/** an exception thrown by emblem library */
class EmblemException(message: String, cause: Exception) extends Exception(message, cause) {

  def this(message: String) { this(message, null) }

}
