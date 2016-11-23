package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains a component */
package object partitionKeyInComponent {

  val subdomain = Subdomain(
    "Partition Key In Component",
    PTypePool(PartitionKeyInComponent),
    CTypePool(Component))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
