package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

package object shorthands {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
