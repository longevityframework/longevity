package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a key that contains a shorthand */
package object partitionKeyWithShorthand {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
