package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a vanilla partition key */
package object partitionKey {

  val subdomain = Subdomain("Partition Key", PTypePool(PartitionKey))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
