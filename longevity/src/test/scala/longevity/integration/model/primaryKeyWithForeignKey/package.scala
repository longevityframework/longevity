package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a primary key that contains a foreign key value */
package object primaryKeyWithForeignKey {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[DomainModel]()

}
