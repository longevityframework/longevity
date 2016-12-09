package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel

/** covers a persistent with a key that contains a component */
package object keyInComponent {

  @domainModel object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
