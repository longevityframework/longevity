package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.ValueType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a key that contains a shorthand */
package object keyWithComponent {

  val subdomain = Subdomain(
    "Key With Component",
    PTypePool(KeyWithComponent),
    ETypePool(ValueType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
