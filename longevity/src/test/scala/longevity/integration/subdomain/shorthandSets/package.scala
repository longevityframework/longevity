package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

package object shorthandSets {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
