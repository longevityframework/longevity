package longevity.integration.subdomain.shorthandSets

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class BooleanShorthand(boolean: Boolean) extends ValueObject

object BooleanShorthand extends ValueType[BooleanShorthand]
