package longevity.exceptions

// TODO rerun scaladocs before signing off on these tickets

/** an exception indicating the a path to a
 * [[longevity.subdomain.RootEntityType#NatKeyProp natural key property]] did
 * not specify a path to a property that can be used as part of a natural key
 */
class InvalidNatKeyPropPathException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
