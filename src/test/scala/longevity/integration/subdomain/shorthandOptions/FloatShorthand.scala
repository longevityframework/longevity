package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class FloatShorthand(float: Float) extends ValueObject

object FloatShorthand extends ValueType[FloatShorthand]
