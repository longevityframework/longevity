package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a set of component entities */
package object componentSet {

  val subdomain = Subdomain(
    "Component Set",
    PTypePool(WithComponentSet),
    CTypePool(CType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
