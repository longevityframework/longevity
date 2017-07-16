package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import longevity.effect.Blocking

/** covers a persistent with a list of associations to another persistent */
package object foreignKeyList {

  @domainModel trait DomainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix[Blocking, DomainModel]()

}
