package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a key that contains multiple properties */
package object keyWithMultipleProperties {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
