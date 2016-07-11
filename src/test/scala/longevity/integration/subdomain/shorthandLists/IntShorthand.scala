package longevity.integration.subdomain.shorthandLists

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class IntShorthand(int: Int) extends ValueObject

object IntShorthand extends ValueType[IntShorthand]
