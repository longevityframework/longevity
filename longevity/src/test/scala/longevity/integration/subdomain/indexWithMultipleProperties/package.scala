package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a index that contains a shorthand */
package object indexWithMultipleProperties {

  val subdomain = Subdomain("Index With Multiple Props", PTypePool(IndexWithMultipleProperties))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
