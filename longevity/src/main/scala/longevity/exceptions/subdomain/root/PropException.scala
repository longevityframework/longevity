package longevity.exceptions.subdomain

/** an exception while building a [[longevity.subdomain.root.Prop property]] */
class PropException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
