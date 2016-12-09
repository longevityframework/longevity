package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a poly type and multiple derived types */
package object derived {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
