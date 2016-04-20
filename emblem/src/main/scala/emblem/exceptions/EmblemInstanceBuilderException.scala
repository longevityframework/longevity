package emblem.exceptions

/** an exception indicating a failure to build an instance with an
 * [[Emblem.InstanceBuilder]]
 */
abstract class EmblemInstanceBuilderException(message: String) extends EmblemException(message)
