package longevity.exceptions.subdomain.root

import longevity.exceptions.subdomain.SubdomainException

/** an exception while building a [[longevity.subdomain.root.Prop property]] */
class PropException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
