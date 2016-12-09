package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

/** covers a persistent with a partition key that contains a foreign key value */
package object partitionKeyWithForeignKey {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
