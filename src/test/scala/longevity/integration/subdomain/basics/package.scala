package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with attributes of every supported basic type */
package object basics {

  val subdomain = Subdomain("Basics", PTypePool(Basics))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
