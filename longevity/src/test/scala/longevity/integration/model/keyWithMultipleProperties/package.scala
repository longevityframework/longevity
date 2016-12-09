package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a key that contains multiple properties */
package object keyWithMultipleProperties {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

}
