package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a secondary key along side a primary key */
package object primaryKeyWithSecondaryKey {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[DomainModel]()

}
