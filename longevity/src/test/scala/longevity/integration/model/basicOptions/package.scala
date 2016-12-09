package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with option attributes for every supported basic type */
package object basicOptions {

  @domainModel object domainModel

  val contexts = TestLongevityConfigs.sparseContextMatrix(domainModel)

  // nice test-only drop-in for debugging:
  // val contexts = {
  //   import longevity.context.LongevityContext
  //   Seq(new LongevityContext(domainModel, TestLongevityConfigs.mongoConfig))
  // }

}
