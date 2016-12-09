package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a single association to another persistent */
package object foreignKey {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

}
