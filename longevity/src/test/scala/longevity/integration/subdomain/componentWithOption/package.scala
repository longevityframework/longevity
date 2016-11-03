package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a single component entity */
package object componentWithOption {

  val subdomain = Subdomain(
    "Component With Option",
    PTypePool(WithComponentWithOption),
    ETypePool(
      EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
