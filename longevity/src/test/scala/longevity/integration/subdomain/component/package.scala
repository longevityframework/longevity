package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a single component entity */
package object component {

  val subdomain = Subdomain("Component", PTypePool(WithComponent), CTypePool(CType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
