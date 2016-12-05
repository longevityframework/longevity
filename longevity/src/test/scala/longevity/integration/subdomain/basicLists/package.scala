package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with list attributes for every supported basic type */
package object basicLists {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
