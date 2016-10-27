package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a vanilla partition key */
package object partitionKey {

  val subdomain = Subdomain("Partition Key", PTypePool(PartitionKey))

  // TODO put sparse matrix back in place
  //val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)
  import longevity.context.LongevityContext
  val contexts = Seq(new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig))

}
