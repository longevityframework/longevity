package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

package object shorthandLists {

  val subdomain = Subdomain(
    "Shorthand Lists",
    PTypePool(ShorthandLists),
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
