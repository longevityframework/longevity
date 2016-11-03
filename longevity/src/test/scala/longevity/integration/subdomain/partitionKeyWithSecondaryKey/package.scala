package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a secondary key along side a partition key */
package object partitionKeyWithSecondaryKey {

  val subdomain = Subdomain("Partition Key With Secondary Key", PTypePool(PartitionKeyWithSecondaryKey))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
