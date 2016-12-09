package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a poly type and multiple derived types */
package object derived {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
