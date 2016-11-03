package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a root entity with a partition key that contains multiple properties */
package object partitionKeyWithMultipleProperties {

  val subdomain = Subdomain(
    "Partition Key With Multiple Properties",
    PTypePool(PartitionKeyWithMultipleProperties),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
