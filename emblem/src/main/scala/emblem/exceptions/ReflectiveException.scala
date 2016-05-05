package emblem.exceptions

/** an exception thrown from misuse of an [[emblem.Emblem Emblem]] or a
 * [[emblem.Union Union]]
 */
abstract class ReflectiveException(message: String, cause: Exception)
extends EmblemException(message, cause) {

  def this(message: String) { this(message, null) }

}
