package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a list of component entities */
package object componentList {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
