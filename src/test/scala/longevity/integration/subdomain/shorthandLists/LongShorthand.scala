package longevity.integration.subdomain.shorthandLists

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class LongShorthand(long: Long) extends ValueObject

object LongShorthand extends ValueType[LongShorthand]
