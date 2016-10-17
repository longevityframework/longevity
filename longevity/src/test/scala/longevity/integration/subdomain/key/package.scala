package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a vanilla key */
package object key {

  val subdomain = Subdomain("Key", PTypePool(Key))
  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
