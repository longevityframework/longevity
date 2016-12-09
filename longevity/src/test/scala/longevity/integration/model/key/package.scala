package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a vanilla key */
package object key {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
