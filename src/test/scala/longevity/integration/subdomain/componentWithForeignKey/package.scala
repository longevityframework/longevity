package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity with an association to another root entity */
package object componentWithForeignKey {

  val subdomain = Subdomain(
    "Component With Foreign Key",
    PTypePool(WithComponentWithForeignKey, Associated),
    ETypePool(
      EType[ComponentWithForeignKey]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
