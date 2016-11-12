package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a partition key that contains multiple properties */
package object partitionKeyWithComplexPartialPartition {

  val subdomain = Subdomain(
    "Partition Key With Complex Partial Partition",
    PTypePool(PartitionKeyWithComplexPartialPartition),
    CTypePool(CType[SubKey]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
