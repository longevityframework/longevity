package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class BooleanShorthand(boolean: Boolean) extends ValueObject

object BooleanShorthand extends ValueType[BooleanShorthand]
