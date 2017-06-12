package longevity.emblem.exceptions

/** an exception thrown by [[emblem.emblematic.Emblem.InstanceBuilder.build
 * Emblem.InstanceBuilder.build]] when a required property was not set by
 * [[emblem.emblematic.Emblem.InstanceBuilder.setProp
 * Emblem.InstanceBuilder.setProp]]
 */
private[longevity] class RequiredPropertyNotSetException(val propName: String)
extends EmblemInstanceBuilderException(
  s"required propery $propName was not set")
