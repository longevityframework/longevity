package longevity.integration.subdomain.shorthandSets

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class StringShorthand(string: String) extends ValueObject

object StringShorthand extends ValueType[StringShorthand]
