package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a list of associations to another persistent */
package object foreignKeyList {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
