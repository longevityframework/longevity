package longevity.integration.subdomain.withComponentWithSinglePropComponent

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Uri(id: String) extends ValueObject

object Uri extends ValueType[Uri]
