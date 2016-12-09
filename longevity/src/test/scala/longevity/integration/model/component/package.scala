package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a single component entity */
package object component {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

}
