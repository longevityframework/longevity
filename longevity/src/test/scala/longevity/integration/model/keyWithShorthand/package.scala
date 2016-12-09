package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a key that contains a shorthand */
package object keyWithShorthand {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

}
