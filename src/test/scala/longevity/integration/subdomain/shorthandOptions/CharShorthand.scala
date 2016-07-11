package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class CharShorthand(char: Char) extends ValueObject

object CharShorthand extends ValueType[CharShorthand]
