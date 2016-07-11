package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Uri(id: String) extends ValueObject

object Uri extends ValueType[Uri]
