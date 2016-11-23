package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

package object shorthandSets {

  val subdomain = Subdomain(
    "Shorthand Sets",
    PTypePool(ShorthandSets),
    CTypePool(
      BooleanShorthand,
      CharShorthand,
      DateTimeShorthand,
      DoubleShorthand,
      FloatShorthand,
      IntShorthand,
      LongShorthand,
      StringShorthand))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
