package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

package object shorthandOptions {

  val subdomain = Subdomain(
    "Shorthand Options",
    PTypePool(ShorthandOptions),
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
