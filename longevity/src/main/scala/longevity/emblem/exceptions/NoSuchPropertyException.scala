package longevity.emblem.exceptions

/** an exception thrown when attempting to retrieve a property by name from an
 * [[emblem.emblematic.Emblem Emblem]] or a [[emblem.emblematic.Union Union]]
 * that doesn't exist
 */
private[longevity] class NoSuchPropertyException(reflectiveName: String, val propName: String)
extends ReflectiveException(s"no such property '$propName' in $reflectiveName")
