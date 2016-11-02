package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a root entity with a key that contains a shorthand */
package object partitionKeyWithShorthand {

  val subdomain = Subdomain(
    "Partition Key With Shorthand",
    PTypePool(PartitionKeyWithShorthand),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
