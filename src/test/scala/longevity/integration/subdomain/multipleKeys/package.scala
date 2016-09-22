package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a key that contains a shorthand */
package object multipleKeys {

  val subdomain = Subdomain("Multiple Keys", PTypePool(MultipleKeys))
  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
