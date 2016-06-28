package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Uri(uri: String) extends ValueObject

object Uri extends ValueType[Uri]
