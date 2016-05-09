package emblem.exceptions

/** an exception thrown from misuse of an [[emblem.emblematic.Emblem Emblem]] or a
 * [[emblem.emblematic.Union Union]]
 */
abstract class ReflectiveException(message: String, cause: Exception)
extends EmblemException(message, cause) {

  def this(message: String) { this(message, null) }

}
