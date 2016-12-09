package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

package object shorthandSets {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
