package emblem.exceptions

/** an exception thrown by [[Emblem.InstanceBuilder.build]] when a required
 * property was not set by [[Emblem.InstanceBuilder.setProp]]
 */
class RequiredPropertyNotSetException(val propName: String)
extends EmblemInstanceBuilderException(
  s"required propery $propName was not set")
