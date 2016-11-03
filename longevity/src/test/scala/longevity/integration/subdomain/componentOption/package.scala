package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with an optional component entity */
package object componentOption {

  val subdomain = Subdomain(
    "Component Option",
    PTypePool(WithComponentOption),
    ETypePool(EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
