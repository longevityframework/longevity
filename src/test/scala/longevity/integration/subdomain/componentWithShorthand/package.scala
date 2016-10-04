package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with an embeddable with an embeddable with a single property  */
package object componentShorthands {

  val subdomain = Subdomain(
    "Component Shorthands",
    PTypePool(WithComponentWithShorthand),
    ETypePool(
      EType[ComponentWithShorthand],
      EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
