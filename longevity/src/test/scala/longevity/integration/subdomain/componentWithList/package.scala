package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with a single component entity */
package object componentWithList {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.mongoOnlyContextMatrix(subdomain)

}
