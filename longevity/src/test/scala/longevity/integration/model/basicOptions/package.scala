package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with option attributes for every supported basic type */
package object basicOptions {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

  // nice test-only drop-in for debugging:
  // val contexts = {
  //   import longevity.context.LongevityContext
  //   Seq(new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig))
  // }

}
