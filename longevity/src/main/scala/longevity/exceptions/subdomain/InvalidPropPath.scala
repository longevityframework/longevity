package longevity.exceptions.subdomain

// TODO mv to InvalidPropException

/** an exception indicating the a path to a
 * [[longevity.subdomain.Prop natural key property]] did
 * not specify a path to a property that can be used as part of a natural key
 */
class InvalidPropPathException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
