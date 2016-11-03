package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a root entity with a key that contains a component */
package object keyWithComponent {

  val subdomain = Subdomain(
    "Key With Component",
    PTypePool(KeyWithComponent),
    ETypePool(EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
