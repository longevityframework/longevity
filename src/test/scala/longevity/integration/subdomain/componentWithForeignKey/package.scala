package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity with an association to another root entity */
package object componentWithForeignKey {

  val subdomain = Subdomain(
    "Component With Foreign Key",
    PTypePool(WithComponentWithForeignKey, Associated),
    ETypePool(
      EntityType[ComponentWithForeignKey]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
