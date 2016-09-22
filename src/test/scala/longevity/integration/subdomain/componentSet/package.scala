package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a set of component entities */
package object componentSet {

  val subdomain = Subdomain(
    "Component Set",
    PTypePool(WithComponentSet),
    ETypePool(EntityType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
