package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a single component entity */
package object shorthandWithComponent {

  val subdomain = Subdomain(
    "Shorthand With Component",
    PTypePool(WithShorthandWithComponent),
    CTypePool(CType[ShorthandWithComponent], CType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
