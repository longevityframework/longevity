package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a primary key that contains a component */
package object primaryKeyWithComponent {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[DomainModel]()

}
