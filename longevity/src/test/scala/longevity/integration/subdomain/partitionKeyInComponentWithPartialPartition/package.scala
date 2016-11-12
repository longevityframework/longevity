package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains a component */
package object partitionKeyInComponentWithPartialPartition {

  val subdomain = Subdomain(
    "Partition Key In Component With Partial Partition",
    PTypePool(PKInComponentWithPartialPartition),
    CTypePool(CType[Component]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
