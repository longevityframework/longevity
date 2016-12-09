package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

package object shorthandOptions {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
