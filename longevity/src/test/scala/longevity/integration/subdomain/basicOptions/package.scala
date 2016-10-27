package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with option attributes for every supported basic type */
package object basicOptions {

  val subdomain = Subdomain("Basic Options", PTypePool(BasicOptions))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

  // nice test-only drop-in for debugging:
  // val contexts = {
  //   import longevity.context.LongevityContext
  //   Seq(new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig))
  // }

}
