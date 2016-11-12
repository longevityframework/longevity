package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a partition key that contains a foreign key value */
package object partitionKeyWithForeignKey {

  val subdomain = Subdomain(
    "Partition Key With Foreign Key",
    PTypePool(PartitionKeyWithForeignKey, Associated),
    CTypePool(CType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
