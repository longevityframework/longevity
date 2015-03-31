package emblem.exceptions

import emblem.TypeKey

/** an exception thrown by a [[emblem.traversors.Traversor]], or one of its cousins in the
 * [[emblem.traversors emblem.traversors package]]
 */
class TraversorException(message: String, cause: Exception) extends EmblemException(message, cause) {

  def this(message: String) { this(message, null) }

}
