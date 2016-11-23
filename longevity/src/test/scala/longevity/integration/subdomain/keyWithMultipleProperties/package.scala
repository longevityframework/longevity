package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains multiple properties */
package object keyWithMultipleProperties {

  val subdomain = Subdomain(
    "Key With Multiple Properties",
    PTypePool(KeyWithMultipleProperties),
    CTypePool(Uri))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
