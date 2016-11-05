package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a partition key that contains multiple properties */
package object partitionKeyWithPartialPartition {

  val subdomain = Subdomain(
    "Partition Key With Partial Partition",
    PTypePool(PartitionKeyWithPartialPartition),
    ETypePool(EType[Uri]))

  //  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)
  val contexts = TestLongevityConfigs.mongoOnlyContextMatrix(subdomain)

}
