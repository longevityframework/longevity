package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a single component entity */
package object componentWithList {

  val subdomain = Subdomain(
    "Component With List",
    PTypePool(WithComponentWithList),
    CTypePool(Component))

  val contexts = TestLongevityConfigs.mongoOnlyContextMatrix(subdomain)

}
