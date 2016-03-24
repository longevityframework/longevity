package longevity.exceptions.subdomain.ptype

/** an exception while building a [[longevity.subdomain.ptype.Prop property]] */
class PropException(message: String, cause: Exception) extends PTypeException(message, cause) {

  def this(message: String) { this(message, null) }

}
