package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a single component entity */
package object componentWithList {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.mongoOnlyContextMatrix(subdomain)

}
