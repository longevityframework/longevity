package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with an embeddable with an embeddable with a single property  */
package object componentShorthands {

  val subdomain = Subdomain(
    "Component Shorthands",
    PTypePool(WithComponentWithShorthand),
    CTypePool(
      CType[ComponentWithShorthand],
      CType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
