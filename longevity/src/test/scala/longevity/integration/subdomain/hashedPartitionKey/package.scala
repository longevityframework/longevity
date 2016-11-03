package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a hashed partition key */
package object hashedPartitionKey {

  val subdomain = Subdomain("Hashed Partition Key", PTypePool(HashedPartitionKey))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
