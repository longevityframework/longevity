package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a partition key that contains a foreign key value */
package object partitionKeyWithForeignKey {

  val subdomain = Subdomain(
    "Partition Key With Foreign Key",
    PTypePool(PartitionKeyWithForeignKey, Associated),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
