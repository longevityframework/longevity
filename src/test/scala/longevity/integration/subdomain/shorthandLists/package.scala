package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EType
import longevity.subdomain.ptype.PTypePool

package object shorthandLists {

  val subdomain = Subdomain(
    "Shorthand Lists",
    PTypePool(ShorthandLists),
    ETypePool(
      EType[BooleanShorthand],
      EType[CharShorthand],
      EType[DateTimeShorthand],
      EType[DoubleShorthand],
      EType[FloatShorthand],
      EType[IntShorthand],
      EType[LongShorthand],
      EType[StringShorthand]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
