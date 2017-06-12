package longevity.emblem.exceptions

/** an exception indicating a failure to build an instance with an
 * [[emblem.emblematic.Emblem.InstanceBuilder Emblem.InstanceBuilder]]
 */
private[longevity] abstract class EmblemInstanceBuilderException(message: String) extends EmblemException(message)
