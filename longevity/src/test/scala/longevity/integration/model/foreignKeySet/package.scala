package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a set of associations to another persistent */
package object foreignKeySet {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
