package longevity.integration.model

import longevity.TestLongevityConfigs
import longevity.model.annotations.subdomain

package object shorthandLists {

  @subdomain object subdomain

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
