package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains a shorthand */
package object keyWithShorthand {

  val subdomain = Subdomain(
    "Key With Shorthand",
    PTypePool(KeyWithShorthand),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
