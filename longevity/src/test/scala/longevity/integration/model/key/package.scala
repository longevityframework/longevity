package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a vanilla key */
package object key {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

}
