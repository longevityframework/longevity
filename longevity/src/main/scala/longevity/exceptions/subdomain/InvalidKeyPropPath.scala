package longevity.exceptions.subdomain

/** an exception indicating the a path to a
 * [[longevity.subdomain.KeyProp natural key property]] did
 * not specify a path to a property that can be used as part of a natural key
 */
class InvalidKeyPropPathException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
