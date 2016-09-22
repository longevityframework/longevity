package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.embeddable.ValueType
import longevity.subdomain.ptype.PTypePool

/** covers a persistent with an embeddable with an embeddable with a single property  */
package object componentShorthands {

  val subdomain = Subdomain(
    "Component Shorthands",
    PTypePool(WithComponentWithShorthand),
    ETypePool(
      EntityType[ComponentWithShorthand],
      ValueType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
