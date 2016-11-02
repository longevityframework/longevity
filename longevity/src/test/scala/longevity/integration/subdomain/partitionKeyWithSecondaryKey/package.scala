package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a vanilla partition key */
package object partitionKeyWithSecondaryKey {

  val subdomain = Subdomain("Partition Key", PTypePool(PartitionKeyWithSecondaryKey))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
