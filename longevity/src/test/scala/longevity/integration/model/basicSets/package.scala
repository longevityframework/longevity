package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with set attributes for every supported basic type */
package object basicSets {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
