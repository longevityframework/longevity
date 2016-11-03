package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a partition key that contains a component */
package object partitionKeyWithComponent {

  val subdomain = Subdomain(
    "Partition Key With Component",
    PTypePool(PartitionKeyWithComponent),
    ETypePool(EType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
