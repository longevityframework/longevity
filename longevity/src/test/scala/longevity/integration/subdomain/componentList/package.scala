package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.CTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a list of component entities */
package object componentList {

  val subdomain = Subdomain(
    "Component List",
    PTypePool(WithComponentList),
    CTypePool(Component))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
