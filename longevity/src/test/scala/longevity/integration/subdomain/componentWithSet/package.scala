package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a single component entity */
package object componentWithSet {

  val subdomain = Subdomain(
    "Component With Set",
    PTypePool(WithComponentWithSet),
    CTypePool(Component))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
