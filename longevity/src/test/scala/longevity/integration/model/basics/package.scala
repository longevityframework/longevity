package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import scala.concurrent.Future

/** covers a persistent with attributes of every supported basic type */
package object basics {

  @domainModel trait DomainModel

  // we use basics to cover all config possibilities. other model tests use a sparse context matrix
  val contexts = TestLongevityConfigs.contextMatrix[Future, DomainModel]()
  //val contexts = TestLongevityConfigs.sqliteOnlyContextMatrix[Future, DomainModel]()

}
