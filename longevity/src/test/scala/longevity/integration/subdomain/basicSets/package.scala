package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with set attributes for every supported basic type */
package object basicSets {

  val subdomain = Subdomain("Basic Sets", PTypePool(BasicSets))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
