package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with list attributes for every supported basic type */
package object basicLists {

  val subdomain = Subdomain("Basic Lists", PTypePool(BasicLists))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
