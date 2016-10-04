package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a list of component entities */
package object componentList {

  val subdomain = Subdomain(
    "Component List",
    PTypePool(WithComponentList),
    ETypePool(EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
