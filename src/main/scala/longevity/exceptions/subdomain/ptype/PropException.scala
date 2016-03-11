package longevity.exceptions.subdomain.ptype

import longevity.exceptions.subdomain.SubdomainException

/** an exception while building a [[longevity.subdomain.ptype.Prop property]] */
class PropException(message: String, cause: Exception)
extends SubdomainException(message, cause) {

  def this(message: String) { this(message, null) }

}
