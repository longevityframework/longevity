package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with attributes of every supported basic type */
package object basics {

  val subdomain = Subdomain("Basics", PTypePool(Basics))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
