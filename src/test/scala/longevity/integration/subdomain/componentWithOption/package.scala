package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity */
package object componentWithOption {

  val subdomain = Subdomain(
    "Component With Option",
    PTypePool(WithComponentWithOption),
    ETypePool(
      EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
