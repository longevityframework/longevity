package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import longevity.effect.Blocking

/** covers a persistent with a single component entity */
package object componentWithList {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[Blocking, DomainModel]()

}
