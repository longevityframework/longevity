package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a set of component entities */
package object componentSet {

  val subdomain = Subdomain(
    "Component Set",
    PTypePool(WithComponentSet),
    ETypePool(EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
