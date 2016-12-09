package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with attributes of every supported basic type */
package object basics {

  @subdomain object subdomain

  // we use basics to cover all config possibilities. other subdomain tests
  // use a sparse context matrix
  val contexts = TestLongevityConfigs.contextMatrix(subdomain)

}
