package emblem.exceptions

/** an exception thrown by [[HasEmblemBuilder.build]] when a required property was not set by
 * [[HasEmblemBuilder.setProp]].
 */
class RequiredPropertyNotSetException(val propName: String)
extends HasEmblemBuilderException(s"required propery $propName was not set")
