package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with a hashed partition key */
package object hashedPartitionKey {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
