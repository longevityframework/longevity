package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains a shorthand */
package object partitionKeyWithShorthand {

  val subdomain = Subdomain(
    "Partition Key With Shorthand",
    PTypePool(PartitionKeyWithShorthand),
    CTypePool(Uri))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
