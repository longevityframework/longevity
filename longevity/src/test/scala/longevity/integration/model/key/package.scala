package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a vanilla key */
package object key {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[DomainModel]()

}
