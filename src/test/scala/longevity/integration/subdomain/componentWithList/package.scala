package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity */
package object componentWithList {

  val subdomain = Subdomain(
    "Component With List",
    PTypePool(WithComponentWithList),
    ETypePool(
      EntityType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
