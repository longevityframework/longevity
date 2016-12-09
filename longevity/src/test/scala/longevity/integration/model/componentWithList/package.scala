package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a single component entity */
package object componentWithList {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.mongoOnlyContextMatrix(subdomain)

}
