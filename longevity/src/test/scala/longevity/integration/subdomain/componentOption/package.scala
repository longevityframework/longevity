package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with an optional component entity */
package object componentOption {

  val subdomain = Subdomain(
    "Component Option",
    PTypePool(WithComponentOption),
    CTypePool(CType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
