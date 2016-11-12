package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

package object shorthands {

  val subdomain = Subdomain(
    "Shorthands",
    PTypePool(Shorthands),
    CTypePool(
      CType[BooleanShorthand],
      CType[CharShorthand],
      CType[DateTimeShorthand],
      CType[DoubleShorthand],
      CType[FloatShorthand],
      CType[IntShorthand],
      CType[LongShorthand],
      CType[StringShorthand]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
