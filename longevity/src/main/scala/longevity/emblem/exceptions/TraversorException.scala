package longevity.emblem.exceptions

/** an exception thrown by a [[emblem.emblematic.traversors.async.Traversor]],
 * or one of its cousins in the [[emblem.emblematic.traversors
 * emblem.emblematic.traversors package]]
 */
private[longevity] abstract class TraversorException(message: String, cause: Exception) extends EmblemException(message, cause) {

  def this(message: String) { this(message, null) }

}
