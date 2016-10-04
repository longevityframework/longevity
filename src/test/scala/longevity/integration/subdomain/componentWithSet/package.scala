package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a root entity with a single component entity */
package object componentWithSet {

  val subdomain = Subdomain(
    "Component With Set",
    PTypePool(WithComponentWithSet),
    ETypePool(
      EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
