package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.ValueType
import longevity.subdomain.ptype.PTypePool

package object shorthandLists {

  val subdomain = Subdomain(
    "Shorthand Lists",
    PTypePool(ShorthandLists),
    ETypePool(
      ValueType[BooleanShorthand],
      ValueType[CharShorthand],
      ValueType[DateTimeShorthand],
      ValueType[DoubleShorthand],
      ValueType[FloatShorthand],
      ValueType[IntShorthand],
      ValueType[LongShorthand],
      ValueType[StringShorthand]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
