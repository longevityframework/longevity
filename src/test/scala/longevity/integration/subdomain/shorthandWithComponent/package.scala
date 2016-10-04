package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.EType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity */
package object shorthandWithComponent {

  val subdomain = Subdomain(
    "Shorthand With Component",
    PTypePool(WithShorthandWithComponent),
    ETypePool(EType[ShorthandWithComponent], EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
